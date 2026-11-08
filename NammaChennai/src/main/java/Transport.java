import java.sql.*;
import java.util.Scanner;

public abstract class Transport {

    private int userId;
    private Scanner scanner;
    private String transportType;

    public Transport(int userId, Scanner scanner, String transportType) {
        this.userId = userId;
        this.scanner = scanner;
        this.transportType = transportType;
    }

    public int getUserId() {
        return userId;
    }

    public String getTransportType() {
        return transportType;
    }

    public abstract double calculateFare(int stops);

    public void showRoutes() {

        String sql = "SELECT * FROM routes WHERE transport_type = ? ORDER BY route_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, transportType);
            ResultSet result = statement.executeQuery();

            System.out.println("\nAvailable " + transportType + " Routes:");

            while (result.next()) {
                System.out.println(result.getInt("route_id") + ". " +
                        result.getString("route_name"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void showStops(int routeId) {

        String sql = "SELECT rs.stop_order, s.station_name " +
                "FROM route_stops rs JOIN stations s " +
                "ON rs.station_id = s.station_id " +
                "WHERE rs.route_id = ? ORDER BY rs.stop_order";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, routeId);
            ResultSet result = statement.executeQuery();

            System.out.println("\n========== ROUTE STOPS ==========");

            while (result.next()) {
                System.out.println(result.getInt("stop_order") + ". " +
                        result.getString("station_name"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void bookTransport() {

        showRoutes();

        System.out.print("Enter Route ID (0 to Back): ");
        int routeId;

        try {
            routeId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Route ID!");
            return;
        }

        if (routeId == 0) {
            return;
        }

        String routeSql = "SELECT route_id FROM routes " +
                "WHERE route_id = ? AND transport_type = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement routeStatement = connection.prepareStatement(routeSql)) {

            routeStatement.setInt(1, routeId);
            routeStatement.setString(2, transportType);

            ResultSet routeResult = routeStatement.executeQuery();

            if (!routeResult.next()) {
                System.out.println("Invalid Route ID!");
                return;
            }

            showStops(routeId);

            int sourceOrder;
            int destinationOrder;

            try {
                System.out.print("Enter Source Stop Number: ");
                sourceOrder = Integer.parseInt(scanner.nextLine());

                System.out.print("Enter Destination Stop Number: ");
                destinationOrder = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid Stop Number!");
                return;
            }

            if (sourceOrder == destinationOrder) {
                System.out.println("Source and Destination cannot be the same!");
                return;
            }

            String stopSql = "SELECT station_id FROM route_stops " +
                    "WHERE route_id = ? AND stop_order = ?";

            int sourceId;
            int destinationId;

            try (PreparedStatement sourceStatement = connection.prepareStatement(stopSql);
                 PreparedStatement destinationStatement = connection.prepareStatement(stopSql)) {

                sourceStatement.setInt(1, routeId);
                sourceStatement.setInt(2, sourceOrder);

                ResultSet sourceResult = sourceStatement.executeQuery();

                if (!sourceResult.next()) {
                    System.out.println("Invalid Source Stop!");
                    return;
                }

                sourceId = sourceResult.getInt("station_id");

                destinationStatement.setInt(1, routeId);
                destinationStatement.setInt(2, destinationOrder);

                ResultSet destinationResult = destinationStatement.executeQuery();

                if (!destinationResult.next()) {
                    System.out.println("Invalid Destination Stop!");
                    return;
                }

                destinationId = destinationResult.getInt("station_id");
            }

            String direction;

            if (sourceOrder < destinationOrder) {
                direction = "Forward";
            } else {
                direction = "Reverse";
            }

            int stops = Math.abs(destinationOrder - sourceOrder);
            double fare = calculateFare(stops);

            System.out.println("\n========== TICKET DETAILS ==========");
            System.out.println("Direction : " + direction);
            System.out.println("Stops     : " + stops);
            System.out.printf("Fare      : Rs.%.2f%n", fare);

            System.out.print("\nEnter Number of Tickets: ");
            int ticketCount;

            try {
                ticketCount = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid Number of Tickets!");
                return;
            }

            if (ticketCount <= 0) {
                System.out.println("Number of Tickets must be greater than zero!");
                return;
            }

            boolean hasPass = StudentPass.hasValidPass(userId);

            double originalAmount =
                    FareCalculator.calculateOriginalAmount(fare, ticketCount);

            double discount =
                    FareCalculator.calculateDiscount(originalAmount, hasPass);

            double totalAmount =
                    FareCalculator.calculateTotalAmount(originalAmount, discount);

            FareCalculator.showFareSummary(
                    fare, ticketCount, originalAmount, discount, totalAmount
            );

            System.out.print("\nConfirm Booking? (yes/no): ");
            String confirmation = scanner.nextLine();

            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Booking Cancelled!");
                return;
            }

            String insertSql = "INSERT INTO transport_bookings " +
                    "(user_id, route_id, source_id, destination_id, direction, " +
                    "stops_count, ticket_count, original_fare, discount_amount, total_fare) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement statement = connection.prepareStatement(
                    insertSql, Statement.RETURN_GENERATED_KEYS)) {

                statement.setInt(1, userId);
                statement.setInt(2, routeId);
                statement.setInt(3, sourceId);
                statement.setInt(4, destinationId);
                statement.setString(5, direction);
                statement.setInt(6, stops);
                statement.setInt(7, ticketCount);
                statement.setDouble(8, originalAmount);
                statement.setDouble(9, discount);
                statement.setDouble(10, totalAmount);

                statement.executeUpdate();

                System.out.println("Transport Ticket Booked Successfully!");

                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        System.out.println("Booking ID: " + generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Booking Failed: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Booking Failed: " + e.getMessage());
        }
    }
}

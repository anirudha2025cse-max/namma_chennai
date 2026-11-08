import java.sql.*;
import java.util.Scanner;

public class RecurringJourney {

    private int userId;
    private Scanner scanner;

    public RecurringJourney(int userId, Scanner scanner) {
        this.userId = userId;
        this.scanner = scanner;
    }

    public void showMenu() {

        while (true) {
            System.out.println("\n========== RECURRING JOURNEYS ==========");
            System.out.println("1. Save a Journey");
            System.out.println("2. View Saved Journeys");
            System.out.println("3. Book a Saved Journey");
            System.out.println("4. Delete a Saved Journey");
            System.out.println("0. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice;

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid Choice!");
                continue;
            }

            switch (choice) {
                case 1:
                    saveJourney();
                    break;
                case 2:
                    viewJourneys();
                    break;
                case 3:
                    bookSavedJourney();
                    break;
                case 4:
                    deleteJourney();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    public void showRoutes() {

        String sql = "SELECT * FROM routes ORDER BY route_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            System.out.println("\n========== AVAILABLE ROUTES ==========");

            while (result.next()) {
                System.out.println(result.getInt("route_id") + ". " +
                        result.getString("route_name") + " (" +
                        result.getString("transport_type") + ")");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void showStops(int routeId) {

        String sql = "SELECT rs.stop_order, s.station_name " +
                "FROM route_stops rs " +
                "JOIN stations s ON rs.station_id = s.station_id " +
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

    public void saveJourney() {

        showRoutes();

        System.out.print("\nEnter Route ID (0 to Back): ");

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

        String routeSql = "SELECT route_id FROM routes WHERE route_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement routeStatement = connection.prepareStatement(routeSql)) {

            routeStatement.setInt(1, routeId);
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

            System.out.print("Enter Journey Name: ");
            String journeyName = scanner.nextLine().trim();

            if (journeyName.isEmpty()) {
                System.out.println("Journey Name cannot be empty!");
                return;
            }

            String insertSql = "INSERT INTO saved_journeys " +
                    "(user_id, journey_name, source_id, destination_id, route_id) " +
                    "VALUES (?, ?, ?, ?, ?)";

            try (PreparedStatement insert = connection.prepareStatement(
                    insertSql, Statement.RETURN_GENERATED_KEYS)) {

                insert.setInt(1, userId);
                insert.setString(2, journeyName);
                insert.setInt(3, sourceId);
                insert.setInt(4, destinationId);
                insert.setInt(5, routeId);

                insert.executeUpdate();

                System.out.println("Journey Saved Successfully!");

                try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        System.out.println("Journey ID: " + generatedKeys.getInt(1));
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void viewJourneys() {

        String sql = "SELECT sj.journey_id, sj.journey_name, " +
                "r.route_name, r.transport_type, " +
                "s1.station_name AS source_name, " +
                "s2.station_name AS destination_name " +
                "FROM saved_journeys sj " +
                "JOIN routes r ON sj.route_id = r.route_id " +
                "JOIN stations s1 ON sj.source_id = s1.station_id " +
                "JOIN stations s2 ON sj.destination_id = s2.station_id " +
                "WHERE sj.user_id = ? ORDER BY sj.journey_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            ResultSet result = statement.executeQuery();

            System.out.println("\n========== MY SAVED JOURNEYS ==========");

            boolean found = false;

            while (result.next()) {
                found = true;

                System.out.println("\nJourney ID : " + result.getInt("journey_id"));
                System.out.println("Name       : " + result.getString("journey_name"));
                System.out.println("Transport  : " + result.getString("transport_type"));
                System.out.println("Route      : " + result.getString("route_name"));
                System.out.println("Source     : " + result.getString("source_name"));
                System.out.println("Destination: " + result.getString("destination_name"));
            }

            if (!found) {
                System.out.println("No Saved Journeys Found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void bookSavedJourney() {

        viewJourneys();

        System.out.print("\nEnter Journey ID (0 to Back): ");

        int journeyId;

        try {
            journeyId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Journey ID!");
            return;
        }

        if (journeyId == 0) {
            return;
        }

        String sql = "SELECT sj.route_id, sj.source_id, sj.destination_id, " +
                "r.transport_type, " +
                "rs1.stop_order AS source_order, " +
                "rs2.stop_order AS destination_order " +
                "FROM saved_journeys sj " +
                "JOIN routes r ON sj.route_id = r.route_id " +
                "JOIN route_stops rs1 ON sj.route_id = rs1.route_id " +
                "AND sj.source_id = rs1.station_id " +
                "JOIN route_stops rs2 ON sj.route_id = rs2.route_id " +
                "AND sj.destination_id = rs2.station_id " +
                "WHERE sj.journey_id = ? AND sj.user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, journeyId);
            statement.setInt(2, userId);

            ResultSet result = statement.executeQuery();

            if (!result.next()) {
                System.out.println("Invalid Journey ID!");
                return;
            }

            int routeId = result.getInt("route_id");
            int sourceId = result.getInt("source_id");
            int destinationId = result.getInt("destination_id");
            int sourceOrder = result.getInt("source_order");
            int destinationOrder = result.getInt("destination_order");
            String transportType = result.getString("transport_type");

            int stops = Math.abs(destinationOrder - sourceOrder);

            if (stops == 0) {
                System.out.println("Invalid Journey Stops!");
                return;
            }

            String direction;

            if (sourceOrder < destinationOrder) {
                direction = "Forward";
            } else {
                direction = "Reverse";
            }

            Transport transport;

            if (transportType.equals("Metro")) {
                transport = new Metro(userId, scanner);
            } else if (transportType.equals("Bus")) {
                transport = new Bus(userId, scanner);
            } else if (transportType.equals("Train")) {
                transport = new Train(userId, scanner);
            } else {
                System.out.println("Unsupported Transport Type!");
                return;
            }

            double fare = transport.calculateFare(stops);

            System.out.println("\n========== TICKET DETAILS ==========");
            System.out.println("Transport : " + transportType);
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

            try (PreparedStatement insert = connection.prepareStatement(
                    insertSql, Statement.RETURN_GENERATED_KEYS)) {

                insert.setInt(1, userId);
                insert.setInt(2, routeId);
                insert.setInt(3, sourceId);
                insert.setInt(4, destinationId);
                insert.setString(5, direction);
                insert.setInt(6, stops);
                insert.setInt(7, ticketCount);
                insert.setDouble(8, originalAmount);
                insert.setDouble(9, discount);
                insert.setDouble(10, totalAmount);

                insert.executeUpdate();

                System.out.println("Recurring Journey Booked Successfully!");

                try (ResultSet generatedKeys = insert.getGeneratedKeys()) {
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

    public void deleteJourney() {

        viewJourneys();

        System.out.print("\nEnter Journey ID to Delete (0 to Back): ");

        int journeyId;

        try {
            journeyId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Journey ID!");
            return;
        }

        if (journeyId == 0) {
            return;
        }

        System.out.print("Confirm Deletion? (yes/no): ");
        String confirmation = scanner.nextLine();

        if (!confirmation.equalsIgnoreCase("yes")) {
            System.out.println("Deletion Cancelled!");
            return;
        }

        String sql = "DELETE FROM saved_journeys " +
                "WHERE journey_id = ? AND user_id = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, journeyId);
            statement.setInt(2, userId);

            int rows = statement.executeUpdate();

            if (rows > 0) {
                System.out.println("Journey Deleted Successfully!");
            } else {
                System.out.println("Journey Not Found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

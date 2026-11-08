import java.sql.*;
import java.util.Scanner;

public class MyBookings {

    private int userId;
    private Scanner scanner;

    public MyBookings(int userId, Scanner scanner) {
        this.userId = userId;
        this.scanner = scanner;
    }

    public void showMenu() {

        while (true) {

            System.out.println("\n========== MY BOOKINGS ==========");
            System.out.println("1. View Transport Bookings");
            System.out.println("2. View Attraction Bookings");
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
                    viewTransportBookings();
                    break;

                case 2:
                    viewAttractionBookings();
                    break;

                case 0:
                    return;

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    public void viewTransportBookings() {

        String sql = "SELECT tb.booking_id, r.route_name, " +
                "r.transport_type, " +
                "s1.station_name AS source_name, " +
                "s2.station_name AS destination_name, " +
                "tb.direction, tb.stops_count, tb.ticket_count, " +
                "tb.original_fare, tb.discount_amount, " +
                "tb.total_fare, tb.booking_time " +
                "FROM transport_bookings tb " +
                "JOIN routes r ON tb.route_id = r.route_id " +
                "JOIN stations s1 ON tb.source_id = s1.station_id " +
                "JOIN stations s2 ON tb.destination_id = s2.station_id " +
                "WHERE tb.user_id = ? " +
                "ORDER BY tb.booking_time DESC, tb.booking_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            ResultSet result = statement.executeQuery();

            System.out.println("\n========== TRANSPORT BOOKINGS ==========");

            boolean found = false;

            while (result.next()) {

                found = true;

                System.out.println("\n----------------------------------------");
                System.out.println("Booking ID      : " + result.getInt("booking_id"));
                System.out.println("Transport       : " + result.getString("transport_type"));
                System.out.println("Route           : " + result.getString("route_name"));
                System.out.println("Source          : " + result.getString("source_name"));
                System.out.println("Destination     : " + result.getString("destination_name"));
                System.out.println("Direction       : " + result.getString("direction"));
                System.out.println("Stops           : " + result.getInt("stops_count"));
                System.out.println("Number of Tickets: " + result.getInt("ticket_count"));

                System.out.printf("Original Amount : Rs.%.2f%n",
                        result.getDouble("original_fare"));

                System.out.printf("Student Discount: Rs.%.2f%n",
                        result.getDouble("discount_amount"));

                System.out.printf("Total Amount    : Rs.%.2f%n",
                        result.getDouble("total_fare"));

                System.out.println("Booking Date    : " +
                        result.getTimestamp("booking_time"));
            }

            if (!found) {
                System.out.println("No Transport Bookings Found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void viewAttractionBookings() {

        String sql = "SELECT ab.booking_id, a.attraction_name, " +
                "a.location, ab.visit_date, ab.ticket_count, " +
                "ab.original_amount, ab.discount_amount, " +
                "ab.total_amount, ab.booking_time " +
                "FROM attraction_bookings ab " +
                "JOIN attractions a ON ab.attraction_id = a.attraction_id " +
                "WHERE ab.user_id = ? " +
                "ORDER BY ab.booking_time DESC, ab.booking_id DESC";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            ResultSet result = statement.executeQuery();

            System.out.println("\n========== ATTRACTION BOOKINGS ==========");

            boolean found = false;

            while (result.next()) {

                found = true;

                System.out.println("\n----------------------------------------");
                System.out.println("Booking ID       : " + result.getInt("booking_id"));
                System.out.println("Attraction       : " + result.getString("attraction_name"));
                System.out.println("Location         : " + result.getString("location"));
                System.out.println("Visit Date       : " + result.getDate("visit_date"));
                System.out.println("Number of Tickets: " + result.getInt("ticket_count"));

                System.out.printf("Original Amount  : Rs.%.2f%n",
                        result.getDouble("original_amount"));

                System.out.printf("Student Discount : Rs.%.2f%n",
                        result.getDouble("discount_amount"));

                System.out.printf("Total Amount     : Rs.%.2f%n",
                        result.getDouble("total_amount"));

                System.out.println("Booking Date     : " +
                        result.getTimestamp("booking_time"));
            }

            if (!found) {
                System.out.println("No Attraction Bookings Found!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

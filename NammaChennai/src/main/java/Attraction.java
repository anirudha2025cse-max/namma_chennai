import java.sql.*;
import java.time.LocalDate;
import java.util.Scanner;

public class Attraction implements Booking {

    private int userId;
    private Scanner scanner;

    public Attraction(int userId, Scanner scanner) {
        this.userId = userId;
        this.scanner = scanner;
    }

    public void showMenu() {

        while (true) {

            System.out.println("\n========== TOURIST ATTRACTIONS ==========");
            System.out.println("1. View Attractions");
            System.out.println("2. Book Attraction Ticket");
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
                    viewAttractions();
                    break;

                case 2:
                    bookTickets();
                    break;

                case 0:
                    return;

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    public void viewAttractions() {

        System.out.println("\n========== CHENNAI TOURIST ATTRACTIONS ==========");

        String sql = "SELECT * FROM attractions ORDER BY attraction_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                System.out.println("\nAttraction ID : " +
                        result.getInt("attraction_id"));

                System.out.println("Name          : " +
                        result.getString("attraction_name"));

                System.out.println("Location      : " +
                        result.getString("location"));

                System.out.println("Ticket Price  : Rs." +
                        result.getDouble("ticket_price"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void viewPaidAttractions() {

        System.out.println("\n========== BOOK ATTRACTION TICKET ==========");

        String sql = "SELECT * FROM attractions " +
                "WHERE ticket_price > 0 ORDER BY attraction_id";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {

            while (result.next()) {

                System.out.println("\nAttraction ID : " +
                        result.getInt("attraction_id"));

                System.out.println("Name          : " +
                        result.getString("attraction_name"));

                System.out.println("Location      : " +
                        result.getString("location"));

                System.out.println("Ticket Price  : Rs." +
                        result.getDouble("ticket_price"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    @Override
    public void bookTickets() {

        viewPaidAttractions();

        System.out.print("\nEnter Attraction ID (0 to Back): ");

        int attractionId;

        try {
            attractionId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Attraction ID!");
            return;
        }

        if (attractionId == 0) {
            return;
        }

        String sql = "SELECT * FROM attractions " +
                "WHERE attraction_id = ? AND ticket_price > 0";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, attractionId);
            ResultSet result = statement.executeQuery();

            if (!result.next()) {
                System.out.println("Invalid Attraction ID!");
                return;
            }

            String attractionName = result.getString("attraction_name");
            double price = result.getDouble("ticket_price");

            System.out.println("Selected Attraction: " + attractionName);
            System.out.printf("Ticket Price: Rs.%.2f%n", price);

            System.out.print("Enter Visit Date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine();

            LocalDate visitDate;

            try {
                visitDate = LocalDate.parse(dateInput);
            } catch (Exception e) {
                System.out.println("Invalid Date Format!");
                return;
            }

            if (visitDate.isBefore(LocalDate.now())) {
                System.out.println("Visit Date cannot be in the past!");
                return;
            }

            System.out.print("Enter Number of Tickets: ");

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
                    FareCalculator.calculateOriginalAmount(price, ticketCount);

            double discount =
                    FareCalculator.calculateDiscount(originalAmount, hasPass);

            double totalAmount =
                    FareCalculator.calculateTotalAmount(originalAmount, discount);

            System.out.println("\nSelected Attraction : " + attractionName);
            System.out.println("Visit Date : " + visitDate);

            FareCalculator.showFareSummary(
                    price, ticketCount, originalAmount, discount, totalAmount
            );

            System.out.print("\nConfirm Booking? (yes/no): ");
            String confirmation = scanner.nextLine();

            if (!confirmation.equalsIgnoreCase("yes")) {
                System.out.println("Booking Cancelled!");
                return;
            }

            String insertSql = "INSERT INTO attraction_bookings " +
                    "(user_id, attraction_id, visit_date, ticket_count, " +
                    "original_amount, discount_amount, total_amount) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement insert = connection.prepareStatement(
                    insertSql, Statement.RETURN_GENERATED_KEYS)) {

                insert.setInt(1, userId);
                insert.setInt(2, attractionId);
                insert.setDate(3, Date.valueOf(visitDate));
                insert.setInt(4, ticketCount);
                insert.setDouble(5, originalAmount);
                insert.setDouble(6, discount);
                insert.setDouble(7, totalAmount);

                insert.executeUpdate();

                System.out.println("Attraction Ticket Booked Successfully!");

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
}
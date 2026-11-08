import java.sql.*;
import java.util.Scanner;

public class StudentPass {

    private int userId;

    public StudentPass(int userId) {
        this.userId = userId;
    }

    public void showMenu(Scanner scanner) {

        while (true) {

            System.out.println("\n========== STUDENT TRAVEL PASS ==========");
            System.out.println("1. Apply for Student Pass");
            System.out.println("2. View My Student Pass");
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
                    applyPass(scanner);
                    break;

                case 2:
                    viewPass();
                    break;

                case 0:
                    return;

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    public void applyPass(Scanner scanner) {

        try {
            if (hasValidPass(userId)) {
                System.out.println("You already have a valid Student Pass!");
                return;
            }
        } catch (IllegalStateException e) {
            System.out.println(e.getMessage());
            return;
        }

        System.out.println("\n========== APPLY FOR STUDENT PASS ==========");

        System.out.print("Enter College Name: ");
        String collegeName = scanner.nextLine().trim();

        System.out.print("Enter Student ID: ");
        String studentId = scanner.nextLine().trim();

        if (collegeName.isEmpty() || studentId.isEmpty()) {
            System.out.println("College Name and Student ID cannot be empty!");
            return;
        }

        String sql = "INSERT INTO student_passes " +
                "(user_id, college_name, student_id, discount_percent, issue_date, expiry_date) " +
                "VALUES (?, ?, ?, 20, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 1 YEAR))";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, collegeName);
            statement.setString(3, studentId);

            statement.executeUpdate();

            System.out.println("\nStudent Pass Applied Successfully!");
            System.out.println("Discount       : 20%");
            System.out.println("Validity       : 1 Year");
            System.out.println("Your discount will be applied automatically!");

        } catch (SQLException e) {
            System.out.println("Student Pass Application Failed: " + e.getMessage());
        }
    }

    public void viewPass() {

        System.out.println("\n========== MY STUDENT PASS ==========");

        String sql = "SELECT * FROM student_passes " +
                "WHERE user_id = ? ORDER BY pass_id DESC LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            ResultSet result = statement.executeQuery();

            if (result.next()) {

                System.out.println("Pass ID          : " + result.getInt("pass_id"));
                System.out.println("College Name     : " + result.getString("college_name"));
                System.out.println("Student ID       : " + result.getString("student_id"));
                System.out.println("Discount         : " +
                        result.getInt("discount_percent") + "%");
                System.out.println("Issue Date       : " + result.getDate("issue_date"));
                System.out.println("Expiry Date      : " + result.getDate("expiry_date"));

                if (hasValidPass(userId)) {
                    System.out.println("Pass Status      : Active");
                } else {
                    System.out.println("Pass Status      : Expired");
                }

            } else {
                System.out.println("You have not applied for a Student Pass!");
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static boolean hasValidPass(int userId) {

        String sql = "SELECT pass_id FROM student_passes " +
                "WHERE user_id = ? " +
                "AND CURDATE() BETWEEN issue_date AND expiry_date " +
                "LIMIT 1";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            ResultSet result = statement.executeQuery();

            return result.next();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Unable to verify Student Pass: " + e.getMessage(), e);
        }
    }
}

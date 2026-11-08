import java.sql.*;
import java.util.Scanner;

public class UserService {

    public User register(Scanner scanner) {

        System.out.println("\n========== REGISTER ==========");

        System.out.print("Enter Name: ");
        String name = scanner.nextLine();

        String username;

        while (true) {
            System.out.print("Enter Username: ");
            username = scanner.nextLine();

            if (username.isBlank()) {
                System.out.println("Username cannot be empty!");
                continue;
            }

            String checkSql = "SELECT user_id FROM users WHERE username = ?";

            try (Connection connection = DBConnection.getConnection();
                 PreparedStatement statement = connection.prepareStatement(checkSql)) {

                statement.setString(1, username);

                ResultSet result = statement.executeQuery();

                if (result.next()) {
                    System.out.println("Username already exists! Please choose another username.");
                } else {
                    break;
                }

            } catch (SQLException e) {
                System.out.println("Registration Failed: " + e.getMessage());
                return null;
            }
        }

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        System.out.print("Enter Phone Number: ");
        String phone = scanner.nextLine();

        if (name.isBlank() || username.isBlank() || password.isBlank()) {
            System.out.println("Name, Username and Password cannot be empty!");
            return null;
        }

        String sql = "INSERT INTO users (name, username, password, phone) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, name);
            statement.setString(2, username);
            statement.setString(3, password);
            statement.setString(4, phone);

            statement.executeUpdate();

            ResultSet result = statement.getGeneratedKeys();

            if (result.next()) {
                int userId = result.getInt(1);

                System.out.println("Registration Successful!");
                System.out.println("Welcome, " + name + "!");

                return new User(userId, name, username, password, phone);
            }

        } catch (SQLException e) {
            System.out.println("Registration Failed: " + e.getMessage());
        }

        return null;
    }

    public User login(Scanner scanner) {

        System.out.println("\n========== LOGIN ==========");

        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet result = statement.executeQuery();

            if (result.next()) {

                User user = new User(
                        result.getInt("user_id"),
                        result.getString("name"),
                        result.getString("username"),
                        result.getString("password"),
                        result.getString("phone")
                );

                System.out.println("Login Successful!");
                System.out.println("Welcome Back, " + user.getName() + "!");

                return user;
            }

            System.out.println("Invalid Username or Password!");

        } catch (SQLException e) {
            System.out.println("Login Failed: " + e.getMessage());
        }

        return null;
    }
}

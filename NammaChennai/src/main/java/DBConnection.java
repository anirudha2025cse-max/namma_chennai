import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/namma_chennai";
        String username = "root";
        String password = "********";

        return DriverManager.getConnection(url, username, password);
    }

    public static void main(String[] args) {
        try (Connection con = getConnection()) {
            System.out.println("Connected to NammaChennai database successfully!");
        } catch (SQLException e) {
            System.out.println("Database connection failed: " + e.getMessage());
        }
    }
}

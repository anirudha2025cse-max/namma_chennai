import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        UserService userService = new UserService();

        while (true) {

            System.out.println("\n====================================");
            System.out.println("          NAMMA CHENNAI");
            System.out.println("         One City.One App.");
            System.out.println("====================================");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("0. Exit");
            System.out.println("====================================");
            System.out.print("Enter your choice: ");

            int choice;

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid Choice!");
                continue;
            }

            User user = null;

            switch (choice) {
                case 1:
                    user = userService.login(scanner);
                    break;

                case 2:
                    user = userService.register(scanner);
                    break;

                case 0:
                    System.out.println("Thank you for using Namma Chennai!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid Choice!");
            }

            if (user != null) {
                showMainMenu(user, scanner);
            }
        }
    }

    public static void showMainMenu(User user, Scanner scanner) {

        int userId = user.getUserId();

        while (true) {

            System.out.println("\n====================================");
            System.out.println("          NAMMA CHENNAI");
            System.out.println("             MAIN MENU");
            System.out.println("====================================");
            System.out.println("Welcome, " + user.getName() + "!");
            System.out.println("1. Book Transport Tickets");
            System.out.println("2. Tourist Attractions");
            System.out.println("3. Student Travel Pass");
            System.out.println("4. Recurring Journeys");
            System.out.println("5. My Bookings");
            System.out.println("0. Logout");
            System.out.println("====================================");
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
                    showTransportMenu(userId, scanner);
                    break;

                case 2:
                    Attraction attraction = new Attraction(userId, scanner);
                    attraction.showMenu();
                    break;

                case 3:
                    StudentPass studentPass = new StudentPass(userId);
                    studentPass.showMenu(scanner);
                    break;

                case 4:
                    RecurringJourney recurringJourney =
                            new RecurringJourney(userId, scanner);
                    recurringJourney.showMenu();
                    break;

                case 5:
                    MyBookings myBookings = new MyBookings(userId, scanner);
                    myBookings.showMenu();
                    break;

                case 0:
                    System.out.println("Logged Out Successfully!");
                    return;

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }

    public static void showTransportMenu(int userId, Scanner scanner) {

        while (true) {

            System.out.println("\n========== BOOK TRANSPORT TICKETS ==========");
            System.out.println("1. Metro Ticket");
            System.out.println("2. MTC Bus Ticket");
            System.out.println("3. MRTS / Suburban Train Ticket");
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
                    Booking metro = new Metro(userId, scanner);
                    metro.bookTickets();
                    break;

                case 2:
                    Booking bus = new Bus(userId, scanner);
                    bus.bookTickets();
                    break;

                case 3:
                    Booking train = new Train(userId, scanner);
                    train.bookTickets();
                    break;

                case 0:
                    return;

                default:
                    System.out.println("Invalid Choice!");
            }
        }
    }
}

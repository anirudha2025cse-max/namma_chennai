import java.util.Scanner;

public class Metro extends Transport implements Booking {

    public Metro(int userId, Scanner scanner) {
        super(userId, scanner, "Metro");
    }

    @Override
    public double calculateFare(int stops) {
        return stops * 10.0;
    }

    @Override
    public void bookTickets() {
        System.out.println("\n========== METRO TICKET BOOKING ==========");
        bookTransport();
    }
}

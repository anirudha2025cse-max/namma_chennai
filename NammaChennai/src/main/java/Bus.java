import java.util.Scanner;

public class Bus extends Transport implements Booking {

    public Bus(int userId, Scanner scanner) {
        super(userId, scanner, "Bus");
    }

    @Override
    public double calculateFare(int stops) {
        return stops * 5.0;
    }

    @Override
    public void bookTickets() {
        System.out.println("\n========== MTC BUS TICKET BOOKING ==========");
        bookTransport();
    }
}

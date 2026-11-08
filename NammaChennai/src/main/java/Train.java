import java.util.Scanner;

public class Train extends Transport implements Booking {

    public Train(int userId, Scanner scanner) {
        super(userId, scanner, "Train");
    }

    @Override
    public double calculateFare(int stops) {
        return stops * 5.0;
    }

    @Override
    public void bookTickets() {
        System.out.println("\n========== MRTS / SUBURBAN TRAIN TICKET BOOKING ==========");
        bookTransport();
    }
}

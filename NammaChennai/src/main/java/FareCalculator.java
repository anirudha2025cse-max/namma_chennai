public class FareCalculator {

    public static double calculateOriginalAmount(double fare, int ticketCount) {
        return fare * ticketCount;
    }

    public static double calculateDiscount(double originalAmount, boolean hasStudentPass) {

        if (hasStudentPass) {
            return originalAmount * 0.20;
        }

        return 0;
    }

    public static double calculateTotalAmount(double originalAmount, double discount) {
        return originalAmount - discount;
    }

    public static void showFareSummary(double fare, int ticketCount,
                                       double originalAmount, double discount,
                                       double totalAmount) {

        System.out.println("\n========== FARE SUMMARY ==========");
        System.out.printf("Fare Per Ticket : Rs.%.2f%n", fare);
        System.out.println("Number of Tickets : " + ticketCount);
        System.out.printf("Original Amount : Rs.%.2f%n", originalAmount);

        if (discount == 0) {
            System.out.println("Student Discount (20%) : N/A");
        } else {
            System.out.printf("Student Discount (20%%) : Rs.%.2f%n", discount);
        }

        System.out.printf("Total Amount : Rs.%.2f%n", totalAmount);
    }
}

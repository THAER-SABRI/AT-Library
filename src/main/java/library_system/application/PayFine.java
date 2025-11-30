package library_system.application;

import java.time.LocalDate;
import library_system.domain.Book;

public class PayFine {
    private final PaymentLedger ledger;
    private final BookRepository repo;
    private static final double DAILY_FINE = 0.5;

    public PayFine(PaymentLedger ledger, BookRepository repo) {
        if (ledger == null || repo == null) throw new IllegalArgumentException("dependencies cannot be null");
        this.ledger = ledger;
        this.repo = repo;
    }

    public double pay(String userId, double amount, LocalDate date) {
        if (userId == null || userId.trim().isEmpty() || date == null) return 0.0;
        if (amount <= 0) {
            return computeTotalFineAt(userId, date);
        }
        ledger.recordPayment(userId, amount, date);
        double totalFine = computeTotalFineAt(userId, date);
        double remaining = Math.max(0.0, totalFine - amount);
        return remaining;
    }

    private double computeTotalFineAt(String userId, LocalDate onDate) {
        double totalFine = 0.0;
        for (Book b : repo.getAll()) {
            if (b.isBorrowed() && userId.equals(b.getBorrowerId()) && b.getDueDate() != null) {
                long overdueDays = onDate.toEpochDay() - b.getDueDate().toEpochDay();
                if (overdueDays > 0) totalFine += overdueDays * DAILY_FINE;
            }
        }
        return totalFine;
    }
}

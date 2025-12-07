package library_system.application;

import java.time.LocalDate;
import java.util.List;
import library_system.domain.Book;

public class ComputeFine {

    private final BookRepository repo;
    private final PaymentLedger paymentLedger;
    private static final double DAILY_FINE = 0.5;

    public ComputeFine(BookRepository repo, PaymentLedger paymentLedger) {
        if (repo == null || paymentLedger == null) {
            throw new IllegalArgumentException("dependencies cannot be null");
        }
        this.repo = repo;
        this.paymentLedger = paymentLedger;
    }

    public double computeOutstanding(String userId, LocalDate today) {
        if (userId == null || userId.trim().isEmpty() || today == null) return 0.0;

        double total = 0.0;
        List<Book> books = repo.getAll();
        for (Book b : books) {
            if (b.isBorrowed()
                    && userId.equals(b.getBorrowerId())
                    && b.getDueDate() != null
                    && b.getDueDate().isBefore(today)) {

                long overdue = today.toEpochDay() - b.getDueDate().toEpochDay();
                if (overdue > 0) total += overdue * DAILY_FINE;
            }
        }

        double paid = paymentLedger.totalPaidForUser(userId);
        return Math.max(0.0, total - paid);
    }
}

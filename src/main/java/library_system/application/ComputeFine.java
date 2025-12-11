package library_system.application;

import java.time.LocalDate;
import java.util.List;

import library_system.domain.Book;
import library_system.domain.CD;

public class ComputeFine {

    private final BookRepository bookRepo;
    private final CdRepository cdRepo;
    private final PaymentLedger paymentLedger;

    public ComputeFine(BookRepository bookRepo, CdRepository cdRepo, PaymentLedger paymentLedger) {

        if (bookRepo == null || cdRepo == null || paymentLedger == null)
            throw new IllegalArgumentException("Dependencies cannot be null");

        this.bookRepo = bookRepo;
        this.cdRepo = cdRepo;
        this.paymentLedger = paymentLedger;
    }

    public double computeOutstanding(String userId, LocalDate today) {
        if (userId == null || userId.trim().isEmpty() || today == null)
            return 0.0;

        double total = 0.0;

        List<Book> books = bookRepo.getAll();
        for (Book b : books) {
            if (b.isBorrowed()
                    && userId.equals(b.getBorrowerId())
                    && b.getDueDate() != null
                    && b.getDueDate().isBefore(today)) {

                long overdue = today.toEpochDay() - b.getDueDate().toEpochDay();
                if (overdue > 0) total += overdue * b.getDailyFine();
            }
        }

        List<CD> cds = cdRepo.getAll();
        for (CD cd : cds) {
            if (cd.isBorrowed()
                    && userId.equals(cd.getBorrowerId())
                    && cd.getDueDate() != null
                    && cd.getDueDate().isBefore(today)) {

                long overdue = today.toEpochDay() - cd.getDueDate().toEpochDay();
                if (overdue > 0) total += overdue * cd.getDailyFine();
            }
        }

        double paid = paymentLedger.totalPaidForUser(userId);
        return Math.max(0.0, total - paid);
    }
}

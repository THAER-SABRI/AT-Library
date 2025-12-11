package library_system.application;

import java.time.LocalDate;
import java.util.List;

import library_system.domain.Book;
import library_system.domain.CD;

/**
 * Service responsible for calculating outstanding fines for a specific user.
 * <p>
 * This class checks all borrowed books and CDs, determines whether any items
 * are overdue, computes the total accumulated fine based on daily fine rates,
 * and subtracts any payments previously recorded in the {@link PaymentLedger}.
 * </p>
 * <p>
 * Fines are computed as:
 * <br>
 * <code>(number of overdue days) × (media daily fine)</code>
 * </p>
 * A user may owe a fine if:
 * <ul>
 *     <li>They have borrowed items that are past their due date</li>
 *     <li>They have not fully paid the accumulated amount</li>
 * </ul>
 */
public class ComputeFine {

    private final BookRepository bookRepo;
    private final CdRepository cdRepo;
    private final PaymentLedger paymentLedger;

    /**
     * Constructs a new {@code ComputeFine} service.
     *
     * @param bookRepo       repository for accessing book records; must not be {@code null}
     * @param cdRepo         repository for accessing CD records; must not be {@code null}
     * @param paymentLedger  ledger used to retrieve recorded fine payments; must not be {@code null}
     *
     * @throws IllegalArgumentException if any dependency is {@code null}
     */
    public ComputeFine(BookRepository bookRepo, CdRepository cdRepo, PaymentLedger paymentLedger) {

        if (bookRepo == null || cdRepo == null || paymentLedger == null)
            throw new IllegalArgumentException("Dependencies cannot be null");

        this.bookRepo = bookRepo;
        this.cdRepo = cdRepo;
        this.paymentLedger = paymentLedger;
    }

    /**
     * Computes the outstanding fine owed by a user as of a specified date.
     * <p>
     * The calculation includes:
     * <ul>
     *     <li>All overdue books borrowed by the user</li>
     *     <li>All overdue CDs borrowed by the user</li>
     *     <li>Daily fine rates associated with each overdue item</li>
     *     <li>Total payments already made by the user</li>
     * </ul>
     *
     * @param userId the ID of the user whose fine is being calculated; must not be {@code null} or blank
     * @param today  the current date used to determine how overdue items are; must not be {@code null}
     *
     * @return the outstanding fine amount (never negative). Returns {@code 0.0}
     *         if the input is invalid or if the user has no unpaid fines.
     */
    public double computeOutstanding(String userId, LocalDate today) {
        if (userId == null || userId.trim().isEmpty() || today == null)
            return 0.0;

        double total = 0.0;

        // -------------------------
        // Calculate overdue fines for Books
        // -------------------------
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

        // -------------------------
        // Calculate overdue fines for CDs
        // -------------------------
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

        // Subtract any payments already recorded for this user
        double paid = paymentLedger.totalPaidForUser(userId);

        // Never return a negative balance
        return Math.max(0.0, total - paid);
    }
}

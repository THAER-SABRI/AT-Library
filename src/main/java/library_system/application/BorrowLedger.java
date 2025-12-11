package library_system.application;

import java.time.LocalDate;
import java.util.List;

/**
 * Ledger interface responsible for recording all borrow and return
 * transactions in the library system.
 * <p>
 * Implementations of this interface track which user borrowed which item,
 * as well as due dates and return events. The stored data may be persisted
 * in files, databases, or in-memory depending on the system configuration.
 * </p>
 */
public interface BorrowLedger {

    /**
     * Records a borrow transaction for a book.
     *
     * @param isbn     the ISBN of the borrowed book; must not be {@code null}.
     * @param userId   the identifier of the user who borrowed the book; must not be {@code null}.
     * @param dueDate  the calculated date by which the book must be returned; must not be {@code null}.
     */
    void recordBorrow(String isbn, String userId, LocalDate dueDate);

    /**
     * Records a return transaction for a book.
     *
     * @param isbn     the ISBN of the returned book; must not be {@code null}.
     * @param userIdW  the identifier of the user returning the book; must not be {@code null}.
     */
    void recordReturn(String isbn, String userIdW);

    /**
     * Retrieves all ledger entries.
     * <p>
     * Each entry is stored as a textual record representing a borrow or return
     * transaction. The format depends on the implementation (for example, a
     * line in a text file).
     * </p>
     *
     * @return a list of string representations of all ledger records; never {@code null}.
     */
    List<String> findAll();
}

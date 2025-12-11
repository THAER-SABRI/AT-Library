package library_system.application;

import java.time.LocalDate;
import java.util.List;

/**
 * Ledger interface for recording and retrieving fine payments made by users.
 * <p>
 * Implementations of this interface persist payment records, compute how much a
 * user has already paid, and provide raw access to all stored ledger entries.
 * The actual storage mechanism (file-based, database, or in-memory) is determined
 * by the implementing class.
 * </p>
 */
public interface PaymentLedger {

    /**
     * Records a fine payment made by a user.
     *
     * @param userId the ID of the user making the payment; must not be {@code null} or empty
     * @param amount the amount paid; should be positive
     * @param date   the date the payment was made; must not be {@code null}
     */
    void recordPayment(String userId, double amount, LocalDate date);

    /**
     * Computes the total amount of money a given user has paid toward fines.
     *
     * @param userId the ID of the user; must not be {@code null}
     * @return the total amount of all recorded payments for this user; never negative
     */
    double totalPaidForUser(String userId);

    /**
     * Retrieves all ledger entries in their raw, stored format.
     * <p>
     * The format of each entry depends on the implementation (e.g., a line from a file).
     * </p>
     *
     * @return a list of string representations of all payment records; never {@code null}
     */
    List<String> findAll();
}

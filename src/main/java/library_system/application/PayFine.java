package library_system.application;

import java.time.LocalDate;

/**
 * Service responsible for processing fine payments made by users.
 * <p>
 * This class records payments in the {@link PaymentLedger} and updates the
 * user's outstanding fine balance by consulting {@link ComputeFine}.
 * </p>
 * <p>
 * If a user attempts to pay a non-positive amount, no payment is recorded and
 * the current outstanding fine is simply returned.
 * </p>
 */
public class PayFine {

    private final PaymentLedger ledger;
    private final ComputeFine computeFine;

    /**
     * Constructs a new {@code PayFine} service.
     *
     * @param ledger      the ledger responsible for persisting fine payments; must not be {@code null}
     * @param computeFine the service used to compute outstanding fines; must not be {@code null}
     *
     * @throws IllegalArgumentException if any dependency is {@code null}
     */
    public PayFine(PaymentLedger ledger, ComputeFine computeFine) {
        if (ledger == null || computeFine == null)
            throw new IllegalArgumentException("dependencies cannot be null");
        this.ledger = ledger;
        this.computeFine = computeFine;
    }

    /**
     * Records a fine payment for a user and returns the updated outstanding balance.
     * <p>
     * Behavior:
     * <ul>
     *     <li>If {@code userId} is invalid or {@code date} is {@code null}, no payment is recorded and {@code 0.0} is returned.</li>
     *     <li>If {@code amount} is less than or equal to zero, no payment is recorded; the current outstanding balance is returned.</li>
     *     <li>Otherwise, the payment is stored in the ledger, and the updated outstanding amount is recomputed.</li>
     * </ul>
     *
     * @param userId the ID of the user making the payment; must not be {@code null} or blank
     * @param amount the payment amount; must be positive to be recorded
     * @param date   the date of payment; must not be {@code null}
     *
     * @return the updated outstanding fine amount after applying the payment;
     *         returns {@code 0.0} if inputs are invalid
     */
    public double pay(String userId, double amount, LocalDate date) {
        if (userId == null || userId.trim().isEmpty() || date == null) return 0.0;

        // If amount is invalid, no payment is recorded.
        if (amount <= 0) {
            return computeFine.computeOutstanding(userId, date);
        }

        ledger.recordPayment(userId, amount, date);

        // Recalculate outstanding balance after payment.
        return computeFine.computeOutstanding(userId, date);
    }
}

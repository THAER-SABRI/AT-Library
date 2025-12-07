package library_system.application;

import java.time.LocalDate;

public class PayFine {

    private final PaymentLedger ledger;
    private final ComputeFine computeFine;

    public PayFine(PaymentLedger ledger, ComputeFine computeFine) {
        if (ledger == null || computeFine == null)
            throw new IllegalArgumentException("dependencies cannot be null");
        this.ledger = ledger;
        this.computeFine = computeFine;
    }

    public double pay(String userId, double amount, LocalDate date) {
        if (userId == null || userId.trim().isEmpty() || date == null) return 0.0;
        if (amount <= 0) {
            return computeFine.computeOutstanding(userId, date);
        }
        ledger.recordPayment(userId, amount, date);
        return computeFine.computeOutstanding(userId, date);
    }
}

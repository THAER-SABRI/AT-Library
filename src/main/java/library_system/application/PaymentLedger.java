package library_system.application;

import java.time.LocalDate;
import java.util.List;

public interface PaymentLedger {
    void recordPayment(String userId, double amount, LocalDate date);
    double totalPaidForUser(String userId);
    List<String> findAll();
}

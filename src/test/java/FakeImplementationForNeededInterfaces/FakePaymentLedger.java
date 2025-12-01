package FakeImplementationForNeededInterfaces;

import library_system.application.PaymentLedger;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class FakePaymentLedger implements PaymentLedger {

    public List<String> logs = new ArrayList<>();

    @Override
    public void recordPayment(String userId, double amount, LocalDate date) {
        logs.add(userId + "|" + amount + "|" + date);
    }

    @Override
    public List<String> findAll() {
        return logs;
    }
}

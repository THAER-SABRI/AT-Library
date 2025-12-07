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
    public double totalPaidForUser(String userId) {
        double sum = 0.0;
        for (String log : logs) {
            String[] parts = log.split("\\|");
            if (parts.length >= 2 && parts[0].equals(userId)) {
                try {
                    sum += Double.parseDouble(parts[1]);
                } catch (Exception ignored) {}
            }
        }
        return sum;
    }

    @Override
    public List<String> findAll() {
        return logs;
    }
}

package library_system;

import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PAYMENT_SERVICE {
    public double pay(String userId, LIBRARY library, double amount, LocalDate today) {
        if (userId == null || library == null || today == null) return 0.0;
        double outstanding = FINE.computeOutstanding(userId, library, today);
        if (amount <= 0) return outstanding;
        double pay = Math.min(amount, outstanding);
        if (pay > 0) STORAGE.appendDataLine(STORAGE.PAYMENTS_FILE, userId + " | " + round2(pay) + " | " + today);
        return FINE.computeOutstanding(userId, library, today);
    }

    private static double round2(double v) {
        return new BigDecimal(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

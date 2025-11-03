package library_system;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class FINE {
    public static double ratePerDay = 1.0;

    public static double computeOutstanding(String userId, LIBRARY library, LocalDate today) {
        double total = 0.0;
        for (BOOK b : library.getBooks()) {
            if (b.isBorrowed() && userId.equals(b.getBorrowerId()) && b.getDueDate() != null) {
                if (today.isAfter(b.getDueDate())) {
                    long days = ChronoUnit.DAYS.between(b.getDueDate(), today);
                    total += days * ratePerDay;
                }
            }
        }
        double paid = 0.0;
        for (String line : STORAGE.readDataLines(STORAGE.PAYMENTS_FILE)) {
        	String[] p = line.split("\\s*\\|\\s*", -1);
            if (p.length >= 2 && p[0].equals(userId)) {
                try { paid += Double.parseDouble(p[1]); } catch(Exception e) {}
            }
        }
        double outstanding = total - paid;
        if (outstanding < 0) outstanding = 0.0;
        return round2(outstanding);
    }

    public static boolean canBorrow(String userId, LIBRARY library, LocalDate today) {
        return computeOutstanding(userId, library, today) == 0.0;
    }

    private static double round2(double v) {
        return new BigDecimal(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}

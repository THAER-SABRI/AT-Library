package library_system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class PAYMENT_SERVICE_TEST {
    private PAYMENT_SERVICE payService;
    private LIBRARY library;

    @BeforeEach
    void setup() {
        payService = new PAYMENT_SERVICE();
        library = new LIBRARY();
        BOOK b = new BOOK("B", "A", "111");
        library.addBook(b);
        b.markBorrowed("1", LocalDate.now().minusDays(35));
    }

    @Test
    void payFineFully() {
        double remain = payService.pay("1", library, 100.0, LocalDate.now());
        assertTrue(remain >= 0);
    }

    @Test
    void payFinePartially() {
        double remain = payService.pay("1", library, 1.0, LocalDate.now());
        assertTrue(remain >= 0);
    }
}

package library_system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.ArrayList;

class FINE_TEST {

    private LIBRARY library;
    private BOOK book;

    @BeforeEach
    void setup() {
        STORAGE.ensureFiles();
        library = new LIBRARY();
        STORAGE.writeDataLines(STORAGE.PAYMENTS_FILE, new ArrayList<>());
        STORAGE.writeDataLines(STORAGE.BOOKS_FILE, new ArrayList<>());
        STORAGE.writeDataLines(STORAGE.BORROWS_FILE, new ArrayList<>());
        book = new BOOK("BOOK1", "THAER", "111");
        library.addBook(book);
    }

    @Test
    void overdueFineApplied() {
        book.markBorrowed("ALI", LocalDate.now().minusDays(10));
        double fine = FINE.computeOutstanding("ALI", library, LocalDate.now());
        assertTrue(fine > 0);
    }

    @Test
    void noFineIfNotOverdue() {
        BOOK b = new BOOK("BOOK2", "HAYA", "222");
        library.addBook(b);
        b.markBorrowed("ALI", LocalDate.now().plusDays(10));
        double fine = FINE.computeOutstanding("ALI", library, LocalDate.now());
        assertEquals(0.0, fine);
    }

    @Test
    void fineIsZeroIfBookNotBorrowed() {
        BOOK b = new BOOK("BOOK3", "THAER", "333");
        library.addBook(b);
        double fine = FINE.computeOutstanding("ALI", library, LocalDate.now());
        assertEquals(0.0, fine);
    }

    @Test
    void multipleBooksOnlyOverdueCounted() {
        BOOK b1 = new BOOK("BOOK2", "ALI", "444");
        BOOK b2 = new BOOK("BOOK3", "HAYA", "555");
        library.addBook(b1);
        library.addBook(b2);
        b1.markBorrowed("ALI", LocalDate.now().minusDays(15));
        b2.markBorrowed("ALI", LocalDate.now().plusDays(15));
        double fine = FINE.computeOutstanding("ALI", library, LocalDate.now());
        assertTrue(fine > 0);
    }

    @Test
    void fineIsZeroIfPaidCompletely() {
        book.markBorrowed("ALI", LocalDate.now().minusDays(5));
        STORAGE.writeDataLines(STORAGE.PAYMENTS_FILE, java.util.Arrays.asList("ALI | 100"));
        double fine = FINE.computeOutstanding("ALI", library, LocalDate.now());
        assertEquals(0.0, fine);
    }
}

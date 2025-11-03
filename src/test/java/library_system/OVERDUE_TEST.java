package library_system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.List;

class OVERDUE_TEST {
    private LIBRARY library;
    private BOOK book1;
    private BOOK book2;

    @BeforeEach
    void setup() {
        library = new LIBRARY();
        book1 = new BOOK("BOOK1", "THAER", "111");
        book2 = new BOOK("BOOK2", "ALI", "222");
        library.addBook(book1);
        library.addBook(book2);

        book1.markBorrowed("HAYA", LocalDate.now().minusDays(40));
        book2.markBorrowed("ALI", LocalDate.now().plusDays(10));   
    }

    @Test
    void listOverdueBooks() {
        List<BOOK> overdue = OVERDUE.list(library, LocalDate.now());
        assertNotNull(overdue);
        assertEquals(1, overdue.size());
        assertEquals("BOOK1", overdue.get(0).getTitle());
    }
}

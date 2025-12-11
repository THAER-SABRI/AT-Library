package library_system.domain.events_test;

import library_system.domain.events.BookBorrowedEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookBorrowedEventTest {

    @Test
    void testEventFields() {
        BookBorrowedEvent e = new BookBorrowedEvent("U1", "ISBN123");

        assertEquals("U1", e.getUserId());
        assertEquals("ISBN123", e.getIsbn());
        assertEquals("BOOK_BORROWED", e.getName());
    }

    @Test
    void testDifferentValues() {
        BookBorrowedEvent e = new BookBorrowedEvent("UserX", "999-ABC");

        assertEquals("UserX", e.getUserId());
        assertEquals("999-ABC", e.getIsbn());
    }

    @Test
    void testNonNull() {
        BookBorrowedEvent e = new BookBorrowedEvent("A", "B");

        assertNotNull(e.getUserId());
        assertNotNull(e.getIsbn());
        assertNotNull(e.getName());
    }
}

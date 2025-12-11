package library_system.domain.events_test;

import library_system.domain.events.BookReturnedEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookReturnedEventTest {

    @Test
    void testEventFields() {
        BookReturnedEvent e = new BookReturnedEvent("U1", "ISBN123");

        assertEquals("U1", e.getUserId());
        assertEquals("ISBN123", e.getIsbn());
        assertEquals("BOOK_RETURNED", e.getName());
    }

    @Test
    void testDifferentValues() {
        BookReturnedEvent e = new BookReturnedEvent("UserX", "999-ABC");

        assertEquals("UserX", e.getUserId());
        assertEquals("999-ABC", e.getIsbn());
    }

    @Test
    void testNonNull() {
        BookReturnedEvent e = new BookReturnedEvent("A", "B");

        assertNotNull(e.getUserId());
        assertNotNull(e.getIsbn());
        assertNotNull(e.getName());
    }
}

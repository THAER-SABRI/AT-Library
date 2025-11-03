package library_system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class BOOK_TEST {

    private BOOK book;

    @BeforeEach
    void setup() {
        book = new BOOK("BOOK1", "THAER", "111");
    }

    @Test
    void initialState() {
        assertEquals("BOOK1", book.getTitle());
        assertEquals("THAER", book.getAuthor());
        assertEquals("111", book.getIsbn());
        assertFalse(book.isBorrowed());
        assertNull(book.getBorrowerId());
        assertNull(book.getDueDate());
    }

    @Test
    void markBorrowedAndReturned() {
        book.markBorrowed("ALI", LocalDate.now().plusDays(28));
        assertTrue(book.isBorrowed());
        assertEquals("ALI", book.getBorrowerId());
        assertNotNull(book.getDueDate());
        book.markReturned();
        assertFalse(book.isBorrowed());
        assertNull(book.getBorrowerId());
        assertNull(book.getDueDate());
    }

    @Test
    void cannotBorrowTwice() {
        book.markBorrowed("HAYA", LocalDate.now().plusDays(28));
        if (book.isBorrowed()) {
            assertTrue(book.isBorrowed());
        } else {
            fail("Book should remain borrowed and cannot be borrowed twice");
        }
    }

    @Test
    void cannotReturnIfNotBorrowed() {
        boolean wasBorrowed = book.isBorrowed();
        book.markReturned();
        assertEquals(wasBorrowed, book.isBorrowed());
    }
}

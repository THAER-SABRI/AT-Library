package library_system.domain_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import library_system.domain.Book;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Book book;

    @BeforeEach
    void setup() {
        book = new Book("BOOK1", "THAER", "111");
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
    void borrowBookSuccessfully() {
        LocalDate due = LocalDate.now().plusDays(28);

        book.borrow("ALI", due);

        assertTrue(book.isBorrowed());
        assertEquals("ALI", book.getBorrowerId());
        assertEquals(due, book.getDueDate());
    }

    @Test
    void cannotBorrowTwice() {
        LocalDate due1 = LocalDate.now().plusDays(28);
        LocalDate due2 = LocalDate.now().plusDays(10);

        book.borrow("HAYA", due1);
        book.borrow("OTHER", due2); 

        assertTrue(book.isBorrowed());
        assertEquals("HAYA", book.getBorrowerId());
        assertEquals(due1, book.getDueDate());
    }

    @Test
    void ignoreBorrowIfUserIdIsInvalid() {
        LocalDate due = LocalDate.now().plusDays(28);

        book.borrow("  ", due); 

        assertFalse(book.isBorrowed());
        assertNull(book.getBorrowerId());
        assertNull(book.getDueDate());
    }

    @Test
    void ignoreBorrowIfDueDateIsNull() {
        book.borrow("ALI", null);

        assertFalse(book.isBorrowed());
        assertNull(book.getBorrowerId());
        assertNull(book.getDueDate());
    }

    @Test
    void returnBookResetsState() {
        LocalDate due = LocalDate.now().plusDays(28);

        book.borrow("ALI", due);
        book.returnBook();

        assertFalse(book.isBorrowed());
        assertNull(book.getBorrowerId());
        assertNull(book.getDueDate());
    }
}

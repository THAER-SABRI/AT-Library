package library_system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

class BORROW_SERVICE_TEST {
    private BORROW_SERVICE service;
    private LIBRARY library;

    @BeforeEach
    void setup() {
        service = new BORROW_SERVICE();
        library = new LIBRARY();
        library.addBook(new BOOK("Book1", "A", "111"));
    }

    @Test
    void borrowBookSuccessfully() {
        assertTrue(service.borrow(library, "111", "10", LocalDate.now()));
        assertTrue(library.findByIsbn("111").isBorrowed());
    }

    @Test
    void cannotBorrowMissingBook() {
        assertFalse(service.borrow(library, "999", "10", LocalDate.now()));
    }

    @Test
    void returnBookSuccessfully() {
        service.borrow(library, "111", "10", LocalDate.now());
        assertTrue(service.returnBook(library, "111"));
        assertFalse(library.findByIsbn("111").isBorrowed());
    }

    @Test
    void cannotReturnInvalidIsbn() {
        assertFalse(service.returnBook(library, "999"));
    }
}

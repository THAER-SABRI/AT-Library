package library_system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BOOK_TEST {

    private BOOK book;

    @BeforeEach
    void setup() {
        book = new BOOK("BOOK1", "THAER", "12345");
    }

    @Test
    void title() {
        assertEquals("BOOK1", book.getTitle());
    }

    @Test
    void author() {
        assertEquals("THAER", book.getAuthor());
    }

    @Test
    void isbn() {
        assertEquals("12345", book.getIsbn());
    }
}

package library_system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;

class LIBRARY_TEST {

    private LIBRARY library;
    private BOOK book;

    @BeforeEach
    void setup() {
        library = new LIBRARY();
        book = new BOOK("BOOK1", "THAER", "12345");
        library.addBook(book);
    }

    @Test
    void searchByTitle() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        library.searchBook("BOOK1");
        assertTrue(out.toString().contains("BOOK1"));
        System.setOut(System.out);
    }

    @Test
    void searchByAuthor() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        library.searchBook("THAER");
        assertTrue(out.toString().contains("THAER"));
        System.setOut(System.out);
    }

    @Test
    void searchNotFound() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
        library.searchBook("UNKNOWN");
        assertTrue(out.toString().contains("No matching books found"));
        System.setOut(System.out);
    }
}

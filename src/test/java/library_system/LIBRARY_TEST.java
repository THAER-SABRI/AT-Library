package library_system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;

class LIBRARY_TEST {
    private LIBRARY library;

    @BeforeEach
    void setup() {
        STORAGE.ensureFiles();
        STORAGE.writeDataLines(STORAGE.BOOKS_FILE, new ArrayList<>());
        library = new LIBRARY();
        library.addBook(new BOOK("BOOK1", "THAER", "111"));
        library.addBook(new BOOK("BOOK2", "ALI", "222"));
        library.addBook(new BOOK("BOOK3", "HAYA", "333"));
        library.refresh();
    }

    @Test
    void addAndSearchBook() {
        assertEquals(3, library.getBooks().size());
        List<BOOK> found = library.search("BOOK1");
        assertTrue(found.size() > 0);
        assertEquals("111", found.get(0).getIsbn());
    }

    @Test
    void preventDuplicateISBN() {
        assertFalse(library.addBook(new BOOK("BOOK1_DUP", "THAER", "111")));
    }

    @Test
    void findBookByIsbn() {
        BOOK b = library.findByIsbn("222");
        assertNotNull(b);
        assertEquals("BOOK2", b.getTitle());
    }

    @Test
    void findByIsbnNotFound() {
        assertNull(library.findByIsbn("999"));
    }
}

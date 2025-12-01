package library_system.infrastructure.persistence_test;

import library_system.domain.Book;
import library_system.infrastructure.persistence.FileBookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBookRepositoryTest {

    @TempDir
    Path tempDir;

    Path booksFile;
    FileBookRepository repo;

    @BeforeEach
    void setup() {
        booksFile = tempDir.resolve("BOOKS.TXT");
        repo = new FileBookRepository(booksFile.toString());
    }

    @Test
    void saveAndLoadSingleFreeBook() {
        Book b = new Book("111", "Book1", "Author1", true);

        repo.saveAll(List.of(b));

        List<Book> list = repo.getAll();
        assertEquals(1, list.size());
        assertEquals("111", list.get(0).getIsbn());
        assertEquals("Book1", list.get(0).getTitle());
        assertEquals("Author1", list.get(0).getAuthor());
        assertFalse(list.get(0).isBorrowed());
    }

    @Test
    void saveAndLoadBorrowedBook() {
        Book b = new Book("222", "Book2", "Author2", true);
        b.borrow("U1", LocalDate.of(2024, 1, 10));

        repo.saveAll(List.of(b));

        List<Book> list = repo.getAll();
        assertEquals(1, list.size());
        Book loaded = list.get(0);

        assertEquals("222", loaded.getIsbn());
        assertTrue(loaded.isBorrowed());
        assertEquals("U1", loaded.getBorrowerId());
        assertEquals(LocalDate.of(2024, 1, 10), loaded.getDueDate());
    }

    @Test
    void malformedLinesAreIgnored() throws Exception {
        java.nio.file.Files.write(booksFile, java.util.Arrays.asList(
                "BAD LINE",
                "| 1 | 2 |",
                "| ISBN | TITLE | AUTHOR | STATUS | USER_ID | DUE_DATE |"
        ));

        List<Book> list = repo.getAll();
        assertTrue(list.isEmpty());
    }

    @Test
    void addBookWorks() {
        Book b = new Book("333", "B3", "A3", true);

        repo.addBook(b);

        List<Book> list = repo.getAll();
        assertEquals(1, list.size());
        assertEquals("333", list.get(0).getIsbn());
    }

    @Test
    void updateBookWorks() {
        Book b = new Book("444", "Old", "Author", true);
        repo.saveAll(List.of(b));

        Book updated = new Book("444", "NewTitle", "NewAuthor", true);

        repo.updateBook(updated);

        List<Book> list = repo.getAll();
        assertEquals(1, list.size());
        assertEquals("NewTitle", list.get(0).getTitle());
        assertEquals("NewAuthor", list.get(0).getAuthor());
    }

    @Test
    void deleteBookWorks() {
        Book b1 = new Book("555", "B5", "A5", true);
        Book b2 = new Book("666", "B6", "A6", true);

        repo.saveAll(List.of(b1, b2));

        repo.deleteBook("555");

        List<Book> list = repo.getAll();
        assertEquals(1, list.size());
        assertEquals("666", list.get(0).getIsbn());
    }

    @Test
    void reloadFromFileClearsAndLoads() {
        Book b1 = new Book("777", "B7", "A7", true);
        repo.saveAll(List.of(b1));

        List<Book> target = new ArrayList<>();
        target.add(new Book("OLD", "OLD", "OLD", true));

        repo.reloadFromFile(target);

        assertEquals(1, target.size());
        assertEquals("777", target.get(0).getIsbn());
    }
}

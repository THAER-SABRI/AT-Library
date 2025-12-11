package library_system.infrastructure.persistence_test;

import library_system.domain.Book;
import library_system.infrastructure.persistence.FileBookRepository;

import library_system.domain.strategy.BorrowDurationStrategy;
import library_system.domain.strategy.FineStrategy;

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

    BorrowDurationStrategy borrowStrategy = () -> 30;
    FineStrategy fineStrategy = () -> 1;

    @BeforeEach
    void setup() {
        booksFile = tempDir.resolve("BOOKS.TXT");
        repo = new FileBookRepository(booksFile.toString());
    }

    private Book make(String isbn, String title, String author) {
        return new Book(isbn, title, author, borrowStrategy, fineStrategy, true);
    }

    @Test
    void saveAndLoadSingleFreeBook() {
        Book b = make("111", "Book1", "Author1");
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
        Book b = make("222", "Book2", "Author2");
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
        Book b = make("333", "B3", "A3");
        repo.addBook(b);

        List<Book> list = repo.getAll();
        assertEquals(1, list.size());
        assertEquals("333", list.get(0).getIsbn());
    }

    @Test
    void updateBookWorks() {
        Book b = make("444", "Old", "Author");
        repo.saveAll(List.of(b));

        Book updated = make("444", "NewTitle", "NewAuthor");
        repo.updateBook(updated);

        List<Book> list = repo.getAll();
        assertEquals(1, list.size());
        assertEquals("NewTitle", list.get(0).getTitle());
        assertEquals("NewAuthor", list.get(0).getAuthor());
    }

    @Test
    void deleteBookWorks() {
        Book b1 = make("555", "B5", "A5");
        Book b2 = make("666", "B6", "A6");

        repo.saveAll(List.of(b1, b2));
        repo.deleteBook("555");

        List<Book> list = repo.getAll();
        assertEquals(1, list.size());
        assertEquals("666", list.get(0).getIsbn());
    }

    @Test
    void reloadFromFileClearsAndLoads() {
        Book b1 = make("777", "B7", "A7");
        repo.saveAll(List.of(b1));

        List<Book> target = new ArrayList<>();
        target.add(make("OLD", "OLD", "OLD"));

        repo.reloadFromFile(target);

        assertEquals(1, target.size());
        assertEquals("777", target.get(0).getIsbn());
    }
}

package library_system.infrastructure.persistence_test;

import library_system.infrastructure.persistence.FileInitializer;
import library_system.infrastructure.persistence.FileBookRepository;
import library_system.domain.Book;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FileInitializerTest {

    @TempDir
    Path tempDir;

    Path booksFile;
    FileInitializer initializer;

    @BeforeEach
    void setup() {
        booksFile = tempDir.resolve("BOOKS.TXT"); 
        initializer = new FileInitializer(booksFile.toString());
    }

    @Test
    void createsDefaultBooksWhenFileDoesNotExist() throws Exception {
        initializer.initialize();

        assertTrue(Files.exists(booksFile));
        
        FileBookRepository repo = new FileBookRepository(booksFile.toString());
        List<Book> books = repo.getAll();

        assertEquals(3, books.size());
        assertEquals("123", books.get(0).getIsbn());
        assertEquals("456", books.get(1).getIsbn());
        assertEquals("789", books.get(2).getIsbn());
    }

    @Test
    void skipsInitializationWhenFileAlreadyExists() throws Exception {
        Files.write(booksFile, Arrays.asList("DUMMY LINE"));

        initializer.initialize();

        List<String> lines = Files.readAllLines(booksFile);
        assertEquals(1, lines.size());
        assertEquals("DUMMY LINE", lines.get(0));
    }

    @Test
    void createsDirectoryIfMissing() {
        Path nestedFile = tempDir.resolve("nested/dir/BOOKS.TXT");
        FileInitializer init2 = new FileInitializer(nestedFile.toString());

        init2.initialize();

        assertTrue(Files.exists(nestedFile));
    }
}

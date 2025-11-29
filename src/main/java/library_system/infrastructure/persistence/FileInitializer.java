package library_system.infrastructure.persistence;

import library_system.domain.Book;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class FileInitializer {

    private static final String BOOKS_FILE = "DATA/BOOKS.TXT";

    public static void initialize() {
        ensureDir();
        Path filePath = Paths.get(BOOKS_FILE);
        if (Files.exists(filePath)) {
            System.out.println("Books file already exists. Initialization skipped.");
            return;
        }

        List<Book> defaultBooks = Arrays.asList(
                new Book("123", "THAER", "BOOK1", true),
                new Book("456", "HAYA", "BOOK2", true),
                new Book("789", "ALI", "BOOK3", true)
        );

        FileBookRepository repo = new FileBookRepository();
        repo.saveAll(defaultBooks);

        System.out.println("Initialized default books into BOOKS.TXT");
    }

    private static void ensureDir() {
        try {
            Path parent = Paths.get("DATA");
            if (!Files.exists(parent)) Files.createDirectories(parent);
        } catch (IOException e) {
            System.out.println("Error creating DATA directory: " + e.getMessage());
        }
    }
}

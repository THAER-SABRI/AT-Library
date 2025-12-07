package library_system.infrastructure.persistence;

import library_system.application.BookRepository;
import library_system.domain.Book;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class FileBookRepository implements BookRepository {

    private final String file;

    public FileBookRepository() {
        this("DATA/BOOKS.TXT");
    }

    public FileBookRepository(String file) {
        this.file = file;
    }

    @Override
    public List<Book> getAll() {
        ensureParentDir();
        List<Book> list = new ArrayList<>();

        Path path = Paths.get(file);
        if (!Files.exists(path)) return list;

        try {
            for (String line : Files.readAllLines(path)) {
                if (!line.startsWith("|") || !line.endsWith("|")) continue;

                String[] parts = line.split("\\|");
                if (parts.length < 7) continue;

                String col0 = parts[1].trim();
                if ("ISBN".equalsIgnoreCase(col0)) continue; // header

                String isbn   = col0;
                String title  = parts[2].trim();
                String author = parts[3].trim();
                String status = parts[4].trim();
                String user   = parts[5].trim();
                String due    = parts[6].trim();

                Book b = new Book(isbn, title, author, true);

                if ("BORROWED".equalsIgnoreCase(status)) {
                    LocalDate d = ("null".equalsIgnoreCase(due) || due.isBlank())
                            ? null
                            : LocalDate.parse(due);
                    b.setBorrowed(true);
                    b.setBorrowerId(user.isBlank() ? null : user);
                    b.setDueDate(d);
                }

                list.add(b);
            }
        } catch (IOException ignored) {}

        return list;
    }

    @Override
    public void saveAll(List<Book> books) {
        ensureParentDir();
        Path path = Paths.get(file);
        try (BufferedWriter w = Files.newBufferedWriter(path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            w.write(row("ISBN", "TITLE", "AUTHOR", "STATUS", "USER_ID", "DUE_DATE"));
            w.newLine();

            for (Book b : books) {
                String status = b.isBorrowed() ? "BORROWED" : "FREE";
                String user = b.getBorrowerId() == null ? "null" : b.getBorrowerId();
                String due = b.getDueDate() == null ? "null" : b.getDueDate().toString();

                w.write(row(b.getIsbn(), b.getTitle(), b.getAuthor(), status, user, due));
                w.newLine();
            }
        } catch (IOException ignored) {}
    }

    public void addBook(Book b) {
        List<Book> list = getAll();
        list.add(b);
        saveAll(list);
    }

    public void updateBook(Book updated) {
        List<Book> list = getAll();
        list.removeIf(b -> b.getIsbn().equals(updated.getIsbn()));
        list.add(updated);
        saveAll(list);
    }

    public void deleteBook(String isbn) {
        List<Book> list = getAll();
        list.removeIf(b -> b.getIsbn().equals(isbn));
        saveAll(list);
    }

    public void reloadFromFile(List<Book> target) {
        target.clear();
        target.addAll(getAll());
    }

    private String row(String... cols) {
        return "| " + String.join(" | ", cols) + " |";
    }

    private void ensureParentDir() {
        try {
            Path p = Paths.get(file);
            Path parent = p.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (IOException ignored) {}
    }
}

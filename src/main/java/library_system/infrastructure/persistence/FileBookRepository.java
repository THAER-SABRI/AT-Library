package library_system.infrastructure.persistence;

import library_system.application.BookRepository;
import library_system.domain.Book;
import library_system.domain.strategy.BorrowDurationStrategy;
import library_system.domain.strategy.FineStrategy;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

/**
 * File-based implementation of {@link BookRepository}.
 * <p>
 * This repository loads and stores books using a simple text-file format with
 * pipe-separated columns. Each line represents a book entry in the following structure:
 *
 * <pre>
 * | ISBN | TITLE | AUTHOR | STATUS | USER_ID | DUE_DATE |
 * </pre>
 *
 * Lines that do not match this format or represent headers are ignored.
 * Borrowing information (borrower and due date) is also reconstructed when loading.
 * </p>
 */
public class FileBookRepository implements BookRepository {

    private final String file;
    private final BorrowDurationStrategy defaultBorrow = () -> 28;
    private final FineStrategy defaultFine = () -> 1;

    /**
     * Creates a new repository linked to a specific file.
     *
     * @param file the path to the file where book data is stored
     */
    public FileBookRepository(String file) {
        this.file = file;
    }

    /**
     * Loads all books from the underlying file.
     * <p>
     * The method:
     * <ul>
     *     <li>Reads each line beginning with {@code |}</li>
     *     <li>Skips header rows</li>
     *     <li>Parses borrow status, borrower ID, and due date</li>
     *     <li>Reconstructs {@link Book} objects using default strategies</li>
     * </ul>
     *
     * @return a list of all books found in the file; never {@code null}
     */
    @Override
    public List<Book> getAll() {
        List<Book> result = new ArrayList<>();
        Path path = Paths.get(file);

        if (!Files.exists(path)) return result;

        try {
            for (String line : Files.readAllLines(path)) {
                if (!line.startsWith("|")) continue;
                if (line.contains("ISBN") && line.contains("TITLE")) continue;

                String[] p = line.split("\\|");
                if (p.length < 7) continue;

                String isbn = p[1].trim();
                String title = p[2].trim();
                String author = p[3].trim();
                String status = p[4].trim();
                String user = p[5].trim();
                String due = p[6].trim();

                Book b = new Book(isbn, title, author, defaultBorrow, defaultFine, true);

                if (status.equalsIgnoreCase("BORROWED")) {
                    LocalDate d = null;
                    try { d = LocalDate.parse(due); } catch (Exception ignored) {}
                    b.setBorrowed(true);
                    b.setBorrowerId(user.isEmpty() ? null : user);
                    b.setDueDate(d);
                }

                result.add(b);
            }
        } catch (Exception ignored) {}

        return result;
    }

    /**
     * Writes all books to the file, completely replacing its content.
     * <p>
     * The file directory is created if missing. Each book is written in the repository's
     * standard pipe-separated format.
     * </p>
     *
     * @param books the list of books to persist; must not be {@code null}
     */
    @Override
    public void saveAll(List<Book> books) {
        Path path = Paths.get(file);
        try {
            Files.createDirectories(path.getParent());
        } catch (Exception ignored) {}

        List<String> out = new ArrayList<>();
        out.add("| ISBN | TITLE | AUTHOR | STATUS | USER_ID | DUE_DATE |");

        for (Book b : books) {
            String status = b.isBorrowed() ? "BORROWED" : "FREE";
            String user = b.getBorrowerId() == null ? "" : b.getBorrowerId();
            String due = b.getDueDate() == null ? "" : b.getDueDate().toString();

            out.add("| " + b.getIsbn() +
                    " | " + b.getTitle() +
                    " | " + b.getAuthor() +
                    " | " + status +
                    " | " + user +
                    " | " + due +
                    " |");
        }

        try {
            Files.write(path, out);
        } catch (IOException ignored) {}
    }

    /**
     * Adds a new book by loading, modifying, and rewriting the file.
     *
     * @param b the book to add; must not be {@code null}
     */
    public void addBook(Book b) {
        List<Book> list = getAll();
        list.add(b);
        saveAll(list);
    }

    /**
     * Updates an existing book entry by replacing the book with the same ISBN.
     *
     * @param updated the updated book object; must not be {@code null}
     */
    public void updateBook(Book updated) {
        List<Book> list = getAll();
        list.removeIf(x -> x.getIsbn().equals(updated.getIsbn()));
        list.add(updated);
        saveAll(list);
    }

    /**
     * Deletes a book record from the file.
     *
     * @param isbn the ISBN of the book to delete; must not be {@code null}
     */
    public void deleteBook(String isbn) {
        List<Book> list = getAll();
        list.removeIf(b -> b.getIsbn().equals(isbn));
        saveAll(list);
    }

    /**
     * Reloads book data into an existing list instance.
     * <p>
     * This is useful when callers want to refresh a list without creating
     * a new list object.
     * </p>
     *
     * @param target the list to be cleared and filled with the latest file contents
     */
    public void reloadFromFile(List<Book> target) {
        target.clear();
        target.addAll(getAll());
    }
}

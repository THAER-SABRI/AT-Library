package library_system.infrastructure.persistence;

import library_system.application.BookRepository;
import library_system.domain.Book;
import library_system.domain.strategy.BorrowDurationStrategy;
import library_system.domain.strategy.FineStrategy;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class FileBookRepository implements BookRepository {

    private final String file;
    private final BorrowDurationStrategy defaultBorrow = () -> 28;
    private final FineStrategy defaultFine = () -> 1;

    public FileBookRepository(String file) {
        this.file = file;
    }

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

    public void addBook(Book b) {
        List<Book> list = getAll();
        list.add(b);
        saveAll(list);
    }

    public void updateBook(Book updated) {
        List<Book> list = getAll();
        list.removeIf(x -> x.getIsbn().equals(updated.getIsbn()));
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
}

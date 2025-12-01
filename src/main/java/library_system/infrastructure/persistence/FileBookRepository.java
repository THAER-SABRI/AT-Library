package library_system.infrastructure.persistence;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import library_system.application.BookRepository;
import library_system.domain.Book;

public class FileBookRepository implements BookRepository {
    private final String filePath;

    private static final int W_ISBN = 12;
    private static final int W_TITLE = 22;
    private static final int W_AUTHOR = 20;
    private static final int W_STATUS = 10;
    private static final int W_USER = 12;
    private static final int W_DUE = 12;
   
    public FileBookRepository() {
        this("DATA/BOOKS.TXT");
    }
    public FileBookRepository(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public List<Book> getAll() {
        ensureParentDir();
        List<Book> list = new ArrayList<>();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) return list;
        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) continue;
                if (line.startsWith("| ISBN")) continue;
                if (!line.startsWith("|")) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 7) continue;
                String isbn = parts[1].trim();
                String title = parts[2].trim();
                String author = parts[3].trim();
                String status = parts[4].trim();
                String user = parts[5].trim();
                String due = parts[6].trim();
                Book b = new Book(isbn, title, author, true);
                if ("BORROWED".equalsIgnoreCase(status) && !user.isEmpty() && !due.isEmpty()) {
                    try {
                        b.borrow(user, LocalDate.parse(due));
                    } catch (Exception ignored) {}
                }
                list.add(b);
            }
        } catch (IOException e) {
            System.out.println("Error reading books file: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void saveAll(List<Book> books) {
        ensureParentDir();
        List<String> lines = new ArrayList<>();
        lines.add(header());
        for (Book b : books) {
            String status = b.isBorrowed() ? "BORROWED" : "FREE";
            String user = b.isBorrowed() && b.getBorrowerId() != null ? b.getBorrowerId() : "";
            String due = b.isBorrowed() && b.getDueDate() != null ? b.getDueDate().toString() : "";
            lines.add(row(b.getIsbn(), b.getTitle(), b.getAuthor(), status, user, due));
        }
        try {
            Files.write(Paths.get(filePath), lines,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }

    public void reloadFromFile(List<Book> targetList) {
        targetList.clear();
        targetList.addAll(getAll());
    }

    public void addBook(Book book) {
        List<Book> books = getAll();
        books.add(book);
        saveAll(books);
    }

    public void updateBook(Book updatedBook) {
        List<Book> books = getAll();
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getIsbn().equals(updatedBook.getIsbn())) {
                books.set(i, updatedBook);
                break;
            }
        }
        saveAll(books);
    }

    public void deleteBook(String isbn) {
        List<Book> books = getAll();
        books.removeIf(b -> b.getIsbn().equals(isbn));
        saveAll(books);
    }

    private String header() {
        return row("ISBN", "TITLE", "AUTHOR", "STATUS", "USER_ID", "DUE_DATE");
    }

    private String row(String isbn, String title, String author, String status, String user, String due) {
        return new StringBuilder()
                .append("| ").append(pad(isbn, W_ISBN)).append(" | ")
                .append(pad(title, W_TITLE)).append(" | ")
                .append(pad(author, W_AUTHOR)).append(" | ")
                .append(pad(status, W_STATUS)).append(" | ")
                .append(pad(user, W_USER)).append(" | ")
                .append(pad(due, W_DUE)).append(" |")
                .toString();
    }

    private String pad(String s, int w) {
        if (s == null) s = "";
        if (s.length() > w) return s.substring(0, w);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < w) sb.append(' ');
        return sb.toString();
    }

    private void ensureParentDir() {
        try {
            Path p = Paths.get(filePath);
            Path parent = p.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (IOException ignored) {}
    }
}

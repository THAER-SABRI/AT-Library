package library_system.application;

import java.time.LocalDate;
import java.util.*;
import library_system.domain.Book;

public class OverdueBooks {
    private final BookRepository repo;
    private final BorrowLedger ledger;

    public OverdueBooks(BookRepository repo, BorrowLedger ledger) {
        this.repo = repo;
        this.ledger = ledger;
    }

    public List<Book> getOverdueBooks(LocalDate today) {
        List<Book> overdue = new ArrayList<>();
        List<Book> books = repo.getAll();
        List<String> records = ledger.findAll();

        for (String line : records) {
            if (line == null || line.trim().isEmpty() || line.startsWith("| TYPE")) continue;
            String[] parts = line.split("\\|");
            if (parts.length < 5) continue;
            String type = parts[1].trim();
            String isbn = parts[2].trim();
            String user = parts[3].trim();
            String dueStr = parts[4].trim();
            if (!"BORROW".equalsIgnoreCase(type)) continue;
            if (dueStr.isEmpty()) continue;

            LocalDate due;
            try { due = LocalDate.parse(dueStr); } catch (Exception e) { continue; }

            if (due.isBefore(today)) {
                for (Book b : books) {
                    if (b.getIsbn().equals(isbn)) {
                        overdue.add(b);
                        break;
                    }
                }
            }
        }
        return overdue;
    }
}

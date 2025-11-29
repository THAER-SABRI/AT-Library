package library_system.application;

import java.time.LocalDate;
import java.util.List;
import library_system.domain.Book;

public class ComputeFine {
    private final BookRepository repo;
    private static final double DAILY_FINE = 0.5;

    public ComputeFine(BookRepository repo) {
        if (repo == null) throw new IllegalArgumentException("repo cannot be null");
        this.repo = repo;
    }

    public double computeOutstanding(String userId, LocalDate today) {
        if (userId == null || userId.trim().isEmpty() || today == null) return 0.0;
        double total = 0.0;
        List<Book> books = repo.getAll();
        for (Book b : books) {
            if (b.isBorrowed() && userId.equals(b.getBorrowerId()) && b.getDueDate() != null && b.getDueDate().isBefore(today)) {
                long overdue = today.toEpochDay() - b.getDueDate().toEpochDay();
                if (overdue > 0) total += overdue * DAILY_FINE;
            }
        }
        return total;
    }
}

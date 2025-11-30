package library_system.application;

import java.time.LocalDate;
import java.util.List;
import library_system.domain.Book;

public class BorrowBook {
    private final BookRepository repo;
    private final BorrowLedger ledger;
    private final SettingsGateway settings;

    public BorrowBook(BookRepository repo, BorrowLedger ledger, SettingsGateway settings) {
        if (repo == null || ledger == null || settings == null)
            throw new IllegalArgumentException("dependencies cannot be null");
        this.repo = repo;
        this.ledger = ledger;
        this.settings = settings;
    }

    public boolean borrow(String isbn, String userId, LocalDate today) {
        if (isbn == null || userId == null || today == null) return false;
        final String needle = isbn.trim();
        if (needle.isEmpty() || userId.trim().isEmpty()) return false;
        List<Book> books = repo.getAll();
        for (Book b : books) {
            if (needle.equals(b.getIsbn()) && !b.isBorrowed()) {
                int days = Math.max(1, settings.getLoanDays());
                b.borrow(userId, today.plusDays(days));
                ledger.recordBorrow(needle, userId, today.plusDays(days));
                repo.saveAll(books);
                return true;
            }
        }
        return false;
    }
}

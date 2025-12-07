package library_system.application;

import java.time.LocalDate;
import java.util.List;
import library_system.domain.Book;

public class BorrowBook {

    private final BookRepository repo;
    private final BorrowLedger ledger;
    private final SettingsGateway settings;
    private final OverdueBooks overdueBooks;

    public BorrowBook(BookRepository repo,
                      BorrowLedger ledger,
                      SettingsGateway settings,
                      OverdueBooks overdueBooks) {

        if (repo == null || ledger == null || settings == null || overdueBooks == null)
            throw new IllegalArgumentException();

        this.repo = repo;
        this.ledger = ledger;
        this.settings = settings;
        this.overdueBooks = overdueBooks;
    }

    public boolean borrow(String isbn, String userId, LocalDate today) {
        if (isbn == null || userId == null || today == null) return false;
        if (isbn.trim().isEmpty() || userId.trim().isEmpty()) return false;

        List<Book> overdueList = overdueBooks.getOverdueBooks(today);
        for (Book ob : overdueList) {
            if (userId.equals(ob.getBorrowerId())) {
                return false;
            }
        }

        List<Book> books = repo.getAll();
        for (Book b : books) {
            if (isbn.equals(b.getIsbn()) && !b.isBorrowed()) {
                int days = Math.max(1, settings.getLoanDays());
                b.borrow(userId, today.plusDays(days));
                ledger.recordBorrow(isbn, userId, today.plusDays(days));
                repo.saveAll(books);
                return true;
            }
        }
        return false;
    }
}

package library_system.application;

import java.time.LocalDate;
import java.util.List;
import library_system.domain.Book;

public class ReturnBook {

    private final BookRepository repo;
    private final BorrowLedger ledger;
    private final ComputeFine computeFine;

    public ReturnBook(BookRepository repo, BorrowLedger ledger, ComputeFine computeFine) {
        if (repo == null || ledger == null || computeFine == null)
            throw new IllegalArgumentException("dependencies cannot be null");
        this.repo = repo;
        this.ledger = ledger;
        this.computeFine = computeFine;
    }

    public boolean returnBook(String isbn, String userId, LocalDate today) {
        if (isbn == null || isbn.trim().isEmpty()
                || userId == null || userId.trim().isEmpty()
                || today == null) {
            return false;
        }

        double outstanding = computeFine.computeOutstanding(userId, today);
        if (outstanding > 0.0) {
            return false;
        }

        List<Book> books = repo.getAll();
        for (Book b : books) {
            if (isbn.equals(b.getIsbn())
                    && b.isBorrowed()
                    && userId.equals(b.getBorrowerId())) {

                b.returnBook();
                repo.saveAll(books);
                ledger.recordReturn(isbn, userId);
                return true;
            }
        }
        return false;
    }
}

package library_system.application;

import java.util.List;
import library_system.domain.Book;

public class ReturnBook {
    private final BookRepository repo;
    private final BorrowLedger ledger;

    public ReturnBook(BookRepository repo, BorrowLedger ledger) {
        if (repo == null || ledger == null) throw new IllegalArgumentException("dependencies cannot be null");
        this.repo = repo;
        this.ledger = ledger;
    }

    public boolean returnBook(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) return false;
        List<Book> books = repo.getAll();
        for (Book b : books) {
            if (isbn.equals(b.getIsbn()) && b.isBorrowed()) {
                b.returnBook();
                ledger.recordReturn(isbn);
                repo.saveAll(books);
                return true;
            }
        }
        return false;
    }
}

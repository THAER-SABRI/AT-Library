package library_system.application;

import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.events.BookReturnedEvent;
import java.time.LocalDate;
import java.util.List;

public class ReturnMedia {
    private final BookRepository bookRepo;
    private final CdRepository cdRepo;
    private final BorrowLedger ledger;
    private final ComputeFine computeFine;
    private final EventDispatcher dispatcher;

    public ReturnMedia(BookRepository bookRepo,
                       CdRepository cdRepo,
                       BorrowLedger ledger,
                       ComputeFine computeFine,
                       EventDispatcher dispatcher) {
        this.bookRepo = bookRepo;
        this.cdRepo = cdRepo;
        this.ledger = ledger;
        this.computeFine = computeFine;
        this.dispatcher = dispatcher;
    }

    public boolean returnMedia(int mediaType, String mediaId, String userId, LocalDate today) {
        if (mediaId == null || mediaId.trim().isEmpty()
                || userId == null || userId.trim().isEmpty()
                || today == null) return false;

        if (computeFine.computeOutstanding(userId, today) > 0.0) return false;

        if (mediaType == 1) return returnBook(mediaId, userId);
        if (mediaType == 2) return returnCd(mediaId, userId);
        return false;
    }

    private boolean returnBook(String isbn, String userId) {
        List<Book> books = bookRepo.getAll();
        for (Book b : books) {
            if (isbn.equals(b.getIsbn()) && b.isBorrowed() && userId.equals(b.getBorrowerId())) {
                b.returnBook();
                bookRepo.saveAll(books);
                ledger.recordReturn(isbn, userId);
                dispatcher.notifyObservers(new BookReturnedEvent(userId, isbn));
                return true;
            }
        }
        return false;
    }

    private boolean returnCd(String id, String userId) {
        List<CD> cds = cdRepo.getAll();
        for (CD cd : cds) {
            if (id.equals(cd.getId()) && cd.isBorrowed() && userId.equals(cd.getBorrowerId())) {
                cd.returnMedia();
                cdRepo.saveAll(cds);
                ledger.recordReturn(id, userId);
                return true;
            }
        }
        return false;
    }
}

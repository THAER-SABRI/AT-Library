package library_system.application;

import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.events.BookBorrowedEvent;
import java.time.LocalDate;
import java.util.List;

public class BorrowMedia {
    private final BookRepository bookRepo;
    private final CdRepository cdRepo;
    private final BorrowLedger ledger;
    private final OverdueMedia overdueMedia;
    private final EventDispatcher dispatcher;

    public BorrowMedia(BookRepository bookRepo,
                       CdRepository cdRepo,
                       BorrowLedger ledger,
                       OverdueMedia overdueMedia,
                       EventDispatcher dispatcher) {
        this.bookRepo = bookRepo;
        this.cdRepo = cdRepo;
        this.ledger = ledger;
        this.overdueMedia = overdueMedia;
        this.dispatcher = dispatcher;
    }

    public boolean borrow(int mediaType, String mediaId, String userId, LocalDate today) {
        if (mediaId == null || userId == null || today == null) return false;
        mediaId = mediaId.trim();
        userId = userId.trim();
        if (mediaId.isEmpty() || userId.isEmpty()) return false;
        if (overdueMedia.userHasOverdues(userId, today)) return false;

        if (mediaType == 1) {
            List<Book> books = bookRepo.getAll();
            Book target = null;
            for (Book b : books) {
                if (b.getIsbn().equalsIgnoreCase(mediaId)) {
                    target = b;
                    break;
                }
            }
            if (target == null || target.isBorrowed()) return false;
            LocalDate due = today.plusDays(target.getBorrowDays());
            target.borrow(userId, due);
            ledger.recordBorrow(target.getId(), userId, due);
            bookRepo.saveAll(books);
            dispatcher.notifyObservers(new BookBorrowedEvent(userId, target.getIsbn()));
            return true;
        }

        if (mediaType == 2) {
            List<CD> cds = cdRepo.getAll();
            CD target = null;
            for (CD cd : cds) {
                if (cd.getId().equalsIgnoreCase(mediaId)) {
                    target = cd;
                    break;
                }
            }
            if (target == null || target.isBorrowed()) return false;
            LocalDate due = today.plusDays(target.getBorrowDays());
            target.borrow(userId, due);
            ledger.recordBorrow(target.getId(), userId, due);
            cdRepo.saveAll(cds);
            return true;
        }

        return false;
    }

    public boolean borrow(String mediaId, String userId, LocalDate today, int mediaType) {
        if (mediaId == null || userId == null || today == null) return false;
        mediaId = mediaId.trim();
        userId = userId.trim();
        if (mediaId.isEmpty() || userId.isEmpty()) return false;
        return false;
    }
}

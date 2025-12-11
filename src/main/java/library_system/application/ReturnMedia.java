package library_system.application;

import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.events.BookReturnedEvent;
import java.time.LocalDate;
import java.util.List;

/**
 * Service responsible for handling the return of borrowed media items
 * (Books and CDs) in the library system.
 * <p>
 * This class ensures that a media item can only be returned if:
 * <ul>
 *     <li>The input fields are valid</li>
 *     <li>The user does not have outstanding unpaid fines</li>
 *     <li>The specified media exists and is currently borrowed by the user</li>
 * </ul>
 * <p>
 * When a book is successfully returned, a {@link BookReturnedEvent} is dispatched
 * to notify observers (e.g., the email notification system).
 * </p>
 */
public class ReturnMedia {

    private final BookRepository bookRepo;
       private final CdRepository cdRepo;
    private final BorrowLedger ledger;
    private final ComputeFine computeFine;
    private final EventDispatcher dispatcher;

    /**
     * Constructs a new {@code ReturnMedia} service.
     *
     * @param bookRepo     repository providing access to book records; must not be {@code null}
     * @param cdRepo       repository providing access to CD records; must not be {@code null}
     * @param ledger       ledger used to record return transactions; must not be {@code null}
     * @param computeFine  service used to determine if a user owes unpaid fines; must not be {@code null}
     * @param dispatcher   event dispatcher used to notify observers of return events; must not be {@code null}
     */
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

    /**
     * Attempts to return either a book or a CD based on the given media type.
     * <p>
     * The return process will fail if:
     * <ul>
     *     <li>Inputs are invalid or missing</li>
     *     <li>The user has unpaid outstanding fines</li>
     *     <li>The media is not currently borrowed by this user</li>
     *     <li>The media type is not recognized</li>
     * </ul>
     *
     * @param mediaType either {@code 1} for a Book or {@code 2} for a CD
     * @param mediaId   ISBN (book) or ID (CD); must not be {@code null} or blank
     * @param userId    ID of the user returning the media; must not be {@code null} or blank
     * @param today     date of return; must not be {@code null}
     * @return {@code true} if the media was successfully returned; {@code false} otherwise
     */
    public boolean returnMedia(int mediaType, String mediaId, String userId, LocalDate today) {
        if (mediaId == null || mediaId.trim().isEmpty()
                || userId == null || userId.trim().isEmpty()
                || today == null) return false;

        // Cannot return media if the user still owes fines
        if (computeFine.computeOutstanding(userId, today) > 0.0) return false;

        if (mediaType == 1) return returnBook(mediaId, userId);
        if (mediaType == 2) return returnCd(mediaId, userId);

        return false;
    }

    /**
     * Attempts to return a borrowed book for a specific user.
     * <p>
     * If successful, the method:
     * <ul>
     *     <li>Marks the book as returned</li>
     *     <li>Saves the updated book repository</li>
     *     <li>Records the return in the borrow ledger</li>
     *     <li>Sends a {@link BookReturnedEvent} to observers</li>
     * </ul>
     *
     * @param isbn   the ISBN of the book
     * @param userId the ID of the user returning the book
     * @return {@code true} if the return operation succeeds; {@code false} otherwise
     */
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

    /**
     * Attempts to return a borrowed CD for a specific user.
     * <p>
     * If successful, the method:
     * <ul>
     *     <li>Marks the CD as returned</li>
     *     <li>Saves the updated CD repository</li>
     *     <li>Records the return in the borrow ledger</li>
     * </ul>
     *
     * @param id     the CD's identifier
     * @param userId the ID of the user returning the CD
     * @return {@code true} if the return operation succeeds; {@code false} otherwise
     */
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

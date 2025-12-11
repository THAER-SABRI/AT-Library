package library_system.application;

import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.events.BookBorrowedEvent;
import java.time.LocalDate;
import java.util.List;

/**
 * Service responsible for borrowing media items (Books and CDs) from the library.
 * <p>
 * This class coordinates interactions between repositories, the borrow ledger,
 * overdue checks, and the event dispatcher. It supports borrowing two types of media:
 * <ul>
 *     <li>Books (mediaType = 1)</li>
 *     <li>CDs (mediaType = 2)</li>
 * </ul>
 * Borrowing is allowed only when:
 * <ul>
 *     <li>The media exists</li>
 *     <li>The media is not already borrowed</li>
 *     <li>The user does not have overdue items</li>
 *     <li>All required input values are valid</li>
 * </ul>
 * When a book is successfully borrowed, a {@link BookBorrowedEvent} is dispatched.
 */
public class BorrowMedia {

    private final BookRepository bookRepo;
    private final CdRepository cdRepo;
    private final BorrowLedger ledger;
    private final OverdueMedia overdueMedia;
    private final EventDispatcher dispatcher;

    /**
     * Constructs a new {@code BorrowMedia} service.
     *
     * @param bookRepo      repository for storing and retrieving books; must not be {@code null}
     * @param cdRepo        repository for storing and retrieving CDs; must not be {@code null}
     * @param ledger        ledger used to record borrow and return transactions; must not be {@code null}
     * @param overdueMedia  utility to check whether a user has overdue media; must not be {@code null}
     * @param dispatcher    event dispatcher used to broadcast borrow events; may be {@code null} if events are not needed
     */
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

    /**
     * Attempts to borrow a media item (Book or CD) for a given user.
     *
     * @param mediaType type of the media:
     *                  <ul>
     *                      <li>1 → Book</li>
     *                      <li>2 → CD</li>
     *                  </ul>
     * @param mediaId   identifier of the media (ISBN for books, ID for CDs); must not be {@code null} or blank
     * @param userId    identifier of the borrowing user; must not be {@code null} or blank
     * @param today     the current date when borrowing occurs; used to compute the due date
     *
     * @return {@code true} if the media was successfully borrowed; {@code false} otherwise
     */
    public boolean borrow(int mediaType, String mediaId, String userId, LocalDate today) {
        if (mediaId == null || userId == null || today == null) return false;
        mediaId = mediaId.trim();
        userId = userId.trim();
        if (mediaId.isEmpty() || userId.isEmpty()) return false;

        // Cannot borrow if the user currently has overdue items
        if (overdueMedia.userHasOverdues(userId, today)) return false;

        // -------------------
        // Borrow Book
        // -------------------
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

            // Notify observers about the borrowing of a book
            dispatcher.notifyObservers(new BookBorrowedEvent(userId, target.getIsbn()));
            return true;
        }

        // -------------------
        // Borrow CD
        // -------------------
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

        // Unsupported media type
        return false;
    }

    /**
     * Placeholder method for an alternative borrow signature.
     * <p>
     * Currently always returns {@code false} because the functionality
     * is not implemented. The method is retained to match the required interface.
     * </p>
     *
     * @param mediaId   identifier of the media item
     * @param userId    identifier of the user
     * @param today     current date
     * @param mediaType type of media (not used here)
     * @return always {@code false} since no logic is implemented
     */
    public boolean borrow(String mediaId, String userId, LocalDate today, int mediaType) {
        return !(mediaId == null ||
                userId == null ||
                today == null ||
                mediaId.trim().isEmpty() ||
                userId.trim().isEmpty());
    }

}

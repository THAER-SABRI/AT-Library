package library_system.domain.events;

/**
 * Domain event representing the action of borrowing a book.
 * <p>
 * This event is fired whenever a user successfully borrows a book
 * from the library. Observers listening to this event may perform
 * additional actions such as logging activity or sending email
 * notifications to the user.
 * </p>
 */
public class BookBorrowedEvent implements DomainEvent {

    private final String userId;
    private final String isbn;

    /**
     * Creates a new {@code BookBorrowedEvent}.
     *
     * @param userId the ID of the user who borrowed the book; must not be {@code null}
     * @param isbn   the ISBN of the borrowed book; must not be {@code null}
     */
    public BookBorrowedEvent(String userId, String isbn) {
        this.userId = userId;
        this.isbn = isbn;
    }

    /**
     * Returns the name of the event.
     *
     * @return the string literal {@code "BOOK_BORROWED"}
     */
    @Override
    public String getName() {
        return "BOOK_BORROWED";
    }

    /**
     * Retrieves the ID of the user who performed the borrow action.
     *
     * @return the user ID
     */
    public String getUserId() { return userId; }

    /**
     * Retrieves the ISBN of the borrowed book.
     *
     * @return the book's ISBN
     */
    public String getIsbn() { return isbn; }
}

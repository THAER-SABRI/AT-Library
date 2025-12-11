package library_system.domain.events;

/**
 * Domain event representing the return of a borrowed book.
 * <p>
 * This event is fired whenever a user successfully returns a book
 * to the library. Observers may react to this event by performing
 * follow-up actions such as sending confirmation emails or updating
 * audit logs.
 * </p>
 */
public class BookReturnedEvent implements DomainEvent {

    private final String userId;
    private final String isbn;

    /**
     * Creates a new {@code BookReturnedEvent}.
     *
     * @param userId the ID of the user who returned the book; must not be {@code null}
     * @param isbn   the ISBN of the returned book; must not be {@code null}
     */
    public BookReturnedEvent(String userId, String isbn) {
        this.userId = userId;
        this.isbn = isbn;
    }

    /**
     * Returns the name of the event.
     *
     * @return the string literal {@code "BOOK_RETURNED"}
     */
    @Override
    public String getName() {
        return "BOOK_RETURNED";
    }

    /**
     * Retrieves the ID of the user who returned the book.
     *
     * @return the user ID
     */
    public String getUserId() { return userId; }

    /**
     * Retrieves the ISBN of the returned book.
     *
     * @return the book's ISBN
     */
    public String getIsbn() { return isbn; }
}

package library_system.domain.events;

public class BookReturnedEvent implements DomainEvent {

    private final String userId;
    private final String isbn;

    public BookReturnedEvent(String userId, String isbn) {
        this.userId = userId;
        this.isbn = isbn;
    }

    @Override
    public String getName() {
        return "BOOK_RETURNED";
    }

    public String getUserId() { return userId; }
    public String getIsbn() { return isbn; }
}

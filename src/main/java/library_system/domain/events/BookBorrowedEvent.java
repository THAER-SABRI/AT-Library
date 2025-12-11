package library_system.domain.events;

public class BookBorrowedEvent implements DomainEvent {

    private final String userId;
    private final String isbn;

    public BookBorrowedEvent(String userId, String isbn) {
        this.userId = userId;
        this.isbn = isbn;
    }

    @Override
    public String getName() {
        return "BOOK_BORROWED";
    }

    public String getUserId() { return userId; }
    public String getIsbn() { return isbn; }
}

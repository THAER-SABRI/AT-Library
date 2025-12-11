package library_system.application;

import library_system.application.UserDirectory;
import library_system.application.BookRepository;
import library_system.application.CdRepository;
import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.events.*;

import java.time.LocalDate;

/**
 * Observer responsible for sending email notifications when specific
 * domain events occur in the library system.
 * <p>
 * This observer listens for:
 * <ul>
 *     <li>{@link BookBorrowedEvent} – triggers an email informing the user that a book was borrowed.</li>
 *     <li>{@link BookReturnedEvent} – triggers an email confirming successful return.</li>
 * </ul>
 * <p>
 * The observer retrieves user email addresses, fetches book details,
 * constructs human-readable email bodies, and uses {@link EmailService}
 * to deliver the notifications.
 * </p>
 */
public class EmailObserver implements Observer {

    private final EmailService email;
    private final UserDirectory users;
    private final BookRepository bookRepo;
    private final CdRepository cdRepo;

    /**
     * Constructs an {@code EmailObserver} used to react to library-related events.
     *
     * @param email     the email service responsible for sending messages; must not be {@code null}
     * @param users     directory for retrieving user email addresses; must not be {@code null}
     * @param bookRepo  repository used to retrieve book information; must not be {@code null}
     * @param cdRepo    repository used to retrieve CD information (currently unused but reserved for future events)
     */
    public EmailObserver(EmailService email,
                         UserDirectory users,
                         BookRepository bookRepo,
                         CdRepository cdRepo) {
        this.email = email;
        this.users = users;
        this.bookRepo = bookRepo;
        this.cdRepo = cdRepo;
    }

    /**
     * Handles incoming domain events and sends appropriate email notifications.
     * <p>
     * Supported events:
     * <ul>
     *     <li>{@link BookBorrowedEvent}: Sends a borrow confirmation email.</li>
     *     <li>{@link BookReturnedEvent}: Sends a return confirmation email.</li>
     * </ul>
     *
     * @param event the domain event fired by the system; must not be {@code null}
     */
    @Override
    public void onEvent(DomainEvent event) {

        // ----------------------------------------------------
        // Book Borrowed Event
        // ----------------------------------------------------
        if (event instanceof BookBorrowedEvent) {
            BookBorrowedEvent e = (BookBorrowedEvent) event;

            Book book = findBook(e.getIsbn());
            if (book == null) return;

            String address = users.getEmail(e.getUserId());
            if (address == null || address.isEmpty()) return;

            String body =
                    "Dear User,\n\n" +
                    "You have successfully borrowed a book from the library.\n\n" +
                    "Book Details:\n" +
                    "• Title: " + book.getTitle() + "\n" +
                    "• ISBN: " + book.getIsbn() + "\n" +
                    "• Borrowed On: " + LocalDate.now() + "\n" +
                    "• Due Date: " + book.getDueDate() + "\n\n" +
                    "Please return the book on or before the due date to avoid fines.\n\n" +
                    "Library System\n";

            email.sendEmail(address, "Book Borrowed", body);
        }

        // ----------------------------------------------------
        // Book Returned Event
        // ----------------------------------------------------
        if (event instanceof BookReturnedEvent) {
            BookReturnedEvent e = (BookReturnedEvent) event;

            Book book = findBook(e.getIsbn());
            if (book == null) return;

            String address = users.getEmail(e.getUserId());
            if (address == null || address.isEmpty()) return;

            String body =
                    "Dear User,\n\n" +
                    "Your book has been successfully returned.\n\n" +
                    "Returned Book Details:\n" +
                    "• Title: " + book.getTitle() + "\n" +
                    "• ISBN: " + book.getIsbn() + "\n" +
                    "• Return Date: " + LocalDate.now() + "\n\n" +
                    "Thank you for using the Library System.\n";

            email.sendEmail(address, "Book Returned", body);
        }
    }

    /**
     * Searches for a book in the repository by its ISBN.
     *
     * @param isbn the ISBN of the desired book; must not be {@code null}
     * @return the matching {@link Book}, or {@code null} if not found
     */
    private Book findBook(String isbn) {
        for (Book b : bookRepo.getAll()) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) return b;
        }
        return null;
    }
}

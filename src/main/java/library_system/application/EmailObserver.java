package library_system.application;

import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.events.*;
import library_system.application.UserDirectory;
import library_system.application.BookRepository;
import library_system.application.CdRepository;

import java.time.LocalDate;

public class EmailObserver implements Observer {

    private final EmailService email;
    private final UserDirectory users;
    private final BookRepository bookRepo;
    private final CdRepository cdRepo;

    public EmailObserver(EmailService email,
                         UserDirectory users,
                         BookRepository bookRepo,
                         CdRepository cdRepo) {
        this.email = email;
        this.users = users;
        this.bookRepo = bookRepo;
        this.cdRepo = cdRepo;
    }

    @Override
    public void onEvent(DomainEvent event) {

        if (event instanceof BookBorrowedEvent) {
            BookBorrowedEvent e = (BookBorrowedEvent) event;

            Book book = findBook(e.getIsbn());
            String address = users.getEmail(e.getUserId());

            String body =
                    "Dear User,\n\n" +
                    "You have successfully borrowed a book from the library.\n\n" +
                    "Book Details:\n" +
                    "• Title: " + book.getTitle() + "\n" +
                    "• ISBN: " + book.getIsbn() + "\n" +
                    "• Borrowed On: " + LocalDate.now() + "\n" +
                    "• Due Date: " + book.getDueDate() + "\n\n" +
                    "Please make sure to return the book on or before the due date to avoid fines.\n\n" +
                    "Library System\n";

            email.sendEmail(address, "Book Borrowed", body);
        }

        if (event instanceof BookReturnedEvent) {
            BookReturnedEvent e = (BookReturnedEvent) event;

            Book book = findBook(e.getIsbn());
            String address = users.getEmail(e.getUserId());

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

    private Book findBook(String isbn) {
        for (Book b : bookRepo.getAll()) {
            if (b.getIsbn().equalsIgnoreCase(isbn)) return b;
        }
        return null;
    }
}

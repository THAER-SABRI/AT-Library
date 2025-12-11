package library_system.domain;

import java.time.LocalDate;

import library_system.domain.strategy.BorrowDurationStrategy;
import library_system.domain.strategy.FineStrategy;

/**
 * Represents a book in the library system.
 * <p>
 * A {@code Book} is a type of {@link Media} with additional metadata such as
 * author name and configurable borrowing/fine strategies. A book may be
 * borrowed by a user, assigned a due date, and later returned, restoring its
 * availability.
 * </p>
 */
public class Book extends Media {

    private String author;
    private BorrowDurationStrategy borrowStrategy;
    private FineStrategy fineStrategy;

    /**
     * Constructs a new {@code Book} with fully customizable strategies.
     *
     * @param isbn            the ISBN identifier of the book; must not be blank
     * @param title           the book title; must not be {@code null}
     * @param author          the author of the book; may be {@code null}
     * @param borrowStrategy  strategy defining the number of borrow days
     * @param fineStrategy    strategy defining the daily fine amount
     * @param dummy           unused flag (placeholder for flexibility)
     */
    public Book(String isbn, String title, String author,
                BorrowDurationStrategy borrowStrategy,
                FineStrategy fineStrategy, boolean dummy) {
        super(isbn.trim(), title);
        this.author = author;
        this.borrowStrategy = borrowStrategy;
        this.fineStrategy = fineStrategy;
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }

    /**
     * Creates a book with default strategies (28 borrow days, 1 NIS fine).
     *
     * @param title  the title of the book
     * @param author the author name
     * @param isbn   the ISBN identifier
     */
    public Book(String title, String author, String isbn) {
        super(isbn.trim(), title);
        this.author = author;
        this.borrowStrategy = () -> 28;
        this.fineStrategy = () -> 1;
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }

    /**
     * Constructs a new {@code Book} with custom borrowing and fine strategies.
     *
     * @param title          the book title
     * @param author         the author of the book
     * @param isbn           the ISBN identifier
     * @param borrowStrategy strategy defining how long the book can be borrowed
     * @param fineStrategy   strategy defining the fine per overdue day
     */
    public Book(String title, String author, String isbn,
                BorrowDurationStrategy borrowStrategy,
                FineStrategy fineStrategy) {
        super(isbn.trim(), title);
        this.author = author;
        this.borrowStrategy = borrowStrategy;
        this.fineStrategy = fineStrategy;
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }

    /** @return the book's ISBN */
    public String getIsbn()       { return id; }

    /** @return the book's title */
    public String getTitle()      { return title; }

    /** @return the name of the book's author */
    public String getAuthor()     { return author; }

    /** @return {@code true} if the book is currently borrowed */
    public boolean isBorrowed()   { return borrowed; }

    /** @return the ID of the user currently borrowing this book */
    public String getBorrowerId() { return borrowerId; }

    /** @return the due date of the borrowed book */
    public LocalDate getDueDate() { return dueDate; }

    /**
     * Sets the borrowed status (used mainly by file-based repositories).
     *
     * @param borrowed whether the book is borrowed
     */
    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    /**
     * Sets the user ID of the borrower.
     *
     * @param borrowerId the user ID
     */
    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    /**
     * Sets the due date for the book.
     *
     * @param dueDate the date by which the book must be returned
     */
    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * Borrows the book for a given user, assigning a due date.
     * <p>
     * Borrowing is ignored if:
     * <ul>
     *     <li>The book is already borrowed</li>
     *     <li>The user ID is invalid</li>
     *     <li>The due date is {@code null}</li>
     * </ul>
     *
     * @param userId  the ID of the borrowing user
     * @param dueDate the assigned due date
     */
    public void borrow(String userId, LocalDate dueDate) {
        if (borrowed) return;
        if (userId == null || userId.trim().isEmpty()) return;
        if (dueDate == null) return;
        this.borrowed = true;
        this.borrowerId = userId;
        this.dueDate = dueDate;
    }

    /**
     * Returns the book, clearing borrower information and due date.
     */
    public void returnBook() {
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }

    /**
     * Retrieves the number of days this book may be borrowed,
     * as defined by its borrowing strategy.
     *
     * @return the allowed borrowing duration in days
     */
    @Override
    public int getBorrowDays() {
        return borrowStrategy.getBorrowDays();
    }

    /**
     * Retrieves the daily fine amount for this book,
     * as defined by its fine strategy.
     *
     * @return the fine per overdue day
     */
    @Override
    public int getDailyFine() {
        return fineStrategy.getDailyFine();
    }
}

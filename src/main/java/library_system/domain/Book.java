package library_system.domain;

import java.time.LocalDate;

import library_system.domain.strategy.BorrowDurationStrategy;
import library_system.domain.strategy.FineStrategy;

public class Book extends Media {

    private String author;
    private BorrowDurationStrategy borrowStrategy;
    private FineStrategy fineStrategy;

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

    public Book(String title, String author, String isbn) {
        super(isbn.trim(), title);
        this.author = author;
        this.borrowStrategy = () -> 28;
        this.fineStrategy = () -> 1;
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }

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

    public String getIsbn()       { return id; }
    public String getTitle()      { return title; }
    public String getAuthor()     { return author; }
    public boolean isBorrowed()   { return borrowed; }
    public String getBorrowerId() { return borrowerId; }
    public LocalDate getDueDate() { return dueDate; }

    public void setBorrowed(boolean borrowed) {
        this.borrowed = borrowed;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void borrow(String userId, LocalDate dueDate) {
        if (borrowed) return;
        if (userId == null || userId.trim().isEmpty()) return;
        if (dueDate == null) return;
        this.borrowed = true;
        this.borrowerId = userId;
        this.dueDate = dueDate;
    }

    public void returnBook() {
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }

    @Override
    public int getBorrowDays() {
        return borrowStrategy.getBorrowDays();
    }

    @Override
    public int getDailyFine() {
        return fineStrategy.getDailyFine();
    }
}

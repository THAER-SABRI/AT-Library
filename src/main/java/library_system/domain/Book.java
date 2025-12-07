package library_system.domain;

import java.time.LocalDate;

public class Book {

    private String isbn;
    private String title;
    private String author;
    private boolean borrowed;
    private String borrowerId;
    private LocalDate dueDate;

    public Book(String isbn, String title, String author, boolean dummy) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("isbn cannot be blank");
        }
        this.isbn = isbn.trim();
        this.title = title;
        this.author = author;
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }

    public Book(String title, String author, String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            throw new IllegalArgumentException("isbn cannot be blank");
        }
        this.isbn = isbn.trim();
        this.title = title;
        this.author = author;
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }

    public String getIsbn()       { return isbn; }
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
        this.borrowed = true;
        this.borrowerId = userId;
        this.dueDate = dueDate;
    }

    public void returnBook() {
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }
}

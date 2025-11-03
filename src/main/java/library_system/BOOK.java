package library_system;

import java.time.LocalDate;

public class BOOK {
    private String title;
    private String author;
    private String isbn;
    private boolean borrowed;
    private String borrowerId;
    private LocalDate dueDate;

    public BOOK(String title, String author, String isbn) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public boolean isBorrowed() { return borrowed; }
    public String getBorrowerId() { return borrowerId; }
    public LocalDate getDueDate() { return dueDate; }

    public void markBorrowed(String borrowerId, LocalDate dueDate) {
        this.borrowed = true;
        this.borrowerId = borrowerId;
        this.dueDate = dueDate;
    }

    public void markReturned() {
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }
}

package library_system.domain;

import java.time.LocalDate;

public class Book {
    private String title;
    private String author;
    private String isbn;
    private boolean borrowed;
    private String borrowerId;
    private LocalDate dueDate;

    public Book(String title, String author, String isbn) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("title cannot be blank");
        if (author == null || author.trim().isEmpty()) throw new IllegalArgumentException("author cannot be blank");
        if (isbn == null || isbn.trim().isEmpty()) throw new IllegalArgumentException("isbn cannot be blank");
        this.title = title.trim();
        this.author = author.trim();
        this.isbn = isbn.trim();
        this.borrowed = false;
    }

    public Book(String isbn, String title, String author, boolean overloadFix) {
        if (!overloadFix) throw new IllegalArgumentException("use overloadFix=true for (isbn,title,author) constructor");
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("title cannot be blank");
        if (author == null || author.trim().isEmpty()) throw new IllegalArgumentException("author cannot be blank");
        if (isbn == null || isbn.trim().isEmpty()) throw new IllegalArgumentException("isbn cannot be blank");
        this.title = title.trim();
        this.author = author.trim();
        this.isbn = isbn.trim();
        this.borrowed = false;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public boolean isBorrowed() { return borrowed; }
    public String getBorrowerId() { return borrowerId; }
    public LocalDate getDueDate() { return dueDate; }

    public void borrow(String userId, LocalDate dueDate) {
        if (borrowed) return;
        if (userId == null || userId.trim().isEmpty()) return;
        if (dueDate == null) return;
        this.borrowed = true;
        this.borrowerId = userId.trim();
        this.dueDate = dueDate;
    }

    public void returnBook() {
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }
}

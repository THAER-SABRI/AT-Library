package library_system.domain;

import java.time.LocalDate;

public abstract class Media {

    protected String id;
    protected String title;
    protected boolean borrowed;
    protected String borrowerId;
    protected LocalDate dueDate;

    public Media(String id, String title) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("id cannot be blank");

        this.id = id.trim();
        this.title = title;
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }

    public String getId()         { return id; }
    public String getTitle()      { return title; }
    public boolean isBorrowed()   { return borrowed; }
    public String getBorrowerId() { return borrowerId; }
    public LocalDate getDueDate() { return dueDate; }

    public void borrow(String userId, LocalDate dueDate) {
        this.borrowed = true;
        this.borrowerId = userId;
        this.dueDate = dueDate;
    }

    public void returnMedia() {
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }
    
    public abstract int getBorrowDays();
    public abstract int getDailyFine();
}

package library_system.domain;

import java.time.LocalDate;

/**
 * Abstract base class representing a media item in the library system.
 * <p>
 * All media types (e.g., {@link Book}, {@link CD}) share a common structure:
 * an identifier, a title, borrowing state, the user who borrowed it, and the
 * due date when it must be returned. Subclasses provide specific borrowing
 * durations and fine rules through strategy implementations.
 * </p>
 */
public abstract class Media {

    protected String id;
    protected String title;
    protected boolean borrowed;
    protected String borrowerId;
    protected LocalDate dueDate;

    /**
     * Constructs a new media item with the given ID and title.
     *
     * @param id    unique identifier for the media; must not be {@code null} or blank
     * @param title the title of the media item; may be {@code null}
     *
     * @throws IllegalArgumentException if {@code id} is blank
     */
    public Media(String id, String title) {
        if (id == null || id.trim().isEmpty())
            throw new IllegalArgumentException("id cannot be blank");

        this.id = id.trim();
        this.title = title;
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }

    /** @return the unique identifier of this media item */
    public String getId()         { return id; }

    /** @return the title of the media item */
    public String getTitle()      { return title; }

    /** @return {@code true} if this media item is currently borrowed */
    public boolean isBorrowed()   { return borrowed; }

    /** @return the ID of the user who borrowed this media item, or {@code null} if not borrowed */
    public String getBorrowerId() { return borrowerId; }

    /** @return the due date for returning the media, or {@code null} if not borrowed */
    public LocalDate getDueDate() { return dueDate; }

    /**
     * Marks this media item as borrowed by a specific user, assigning a due date.
     *
     * @param userId  the ID of the user borrowing the media
     * @param dueDate the date on which the media is due for return
     */
    public void borrow(String userId, LocalDate dueDate) {
        this.borrowed = true;
        this.borrowerId = userId;
        this.dueDate = dueDate;
    }

    /**
     * Returns the media item, clearing its borrower information and due date.
     */
    public void returnMedia() {
        this.borrowed = false;
        this.borrowerId = null;
        this.dueDate = null;
    }

    /**
     * Retrieves the number of days this media item may be borrowed.
     * Subclasses provide specific rules via strategy implementations.
     *
     * @return the allowed borrowing duration in days
     */
    public abstract int getBorrowDays();

    /**
     * Retrieves the daily fine charged when this media item becomes overdue.
     * Subclasses provide specific fine rules via strategy implementations.
     *
     * @return the fine amount per overdue day
     */
    public abstract int getDailyFine();
}

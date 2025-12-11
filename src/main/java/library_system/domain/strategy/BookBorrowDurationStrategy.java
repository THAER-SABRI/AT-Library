package library_system.domain.strategy;

import library_system.application.SettingsGateway;

/**
 * Borrow duration strategy for books.
 * <p>
 * This strategy retrieves the loan duration for books from the system
 * configuration via a {@link SettingsGateway}. The number of days a
 * book may be borrowed is determined dynamically based on system settings.
 * </p>
 */
public class BookBorrowDurationStrategy implements BorrowDurationStrategy {

    private final SettingsGateway settings;

    /**
     * Creates a new {@code BookBorrowDurationStrategy}.
     *
     * @param settings the settings gateway that provides the configured loan duration;
     *                 must not be {@code null}
     */
    public BookBorrowDurationStrategy(SettingsGateway settings) {
        this.settings = settings;
    }

    /**
     * Returns the number of days a book may be borrowed.
     * <p>
     * The value is retrieved from system settings, allowing the loan duration
     * to be configurable.
     * </p>
     *
     * @return the configured borrowing period for books, in days
     */
    @Override
    public int getBorrowDays() {
        return settings.getLoanDays();
    }
}

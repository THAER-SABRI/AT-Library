package library_system.domain.strategy;

import library_system.application.SettingsGateway;

/**
 * Borrow duration strategy for CDs.
 * <p>
 * This strategy retrieves the CD borrowing period from the system
 * configuration via a {@link SettingsGateway}. This allows CD loan
 * durations to be adjusted without modifying system logic.
 * </p>
 */
public class CdBorrowDurationStrategy implements BorrowDurationStrategy {

    private final SettingsGateway settings;

    /**
     * Creates a new {@code CdBorrowDurationStrategy}.
     *
     * @param settings the gateway providing system configuration values;
     *                 must not be {@code null}
     */
    public CdBorrowDurationStrategy(SettingsGateway settings) {
        this.settings = settings;
    }

    /**
     * Returns the number of days a CD may be borrowed.
     * <p>
     * The value is retrieved from the system's configured CD loan duration.
     * </p>
     *
     * @return the configured borrowing period for CDs, in days
     */
    @Override
    public int getBorrowDays() {
        return settings.getCdLoanDays();
    }
}

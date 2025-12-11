package library_system.domain.strategy;

import library_system.application.SettingsGateway;

/**
 * Fine calculation strategy for CDs.
 * <p>
 * This implementation applies a fixed fine of 20 units (e.g., NIS)
 * per overdue day for borrowed CDs. The fine value is constant and
 * not dependent on system settings.
 * </p>
 */
public class CdFineStrategy implements FineStrategy {

    /**
     * Returns the daily fine amount for overdue CDs.
     *
     * @return a fixed value of {@code 20} representing the fine per overdue day
     */
    @Override
    public int getDailyFine() {
        return 20;
    }
}

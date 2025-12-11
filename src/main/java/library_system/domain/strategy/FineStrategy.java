package library_system.domain.strategy;

/**
 * Strategy interface for determining the daily overdue fine
 * for a media item (e.g., books or CDs).
 * <p>
 * Implementations may return a fixed fine amount or compute it
 * dynamically based on configuration or media-specific rules.
 * </p>
 */
public interface FineStrategy {

    /**
     * Returns the amount charged per day when a media item
     * becomes overdue.
     *
     * @return the daily fine amount
     */
    int getDailyFine();
}

package library_system.domain.strategy;

/**
 * Strategy interface defining how many days a media item may be borrowed.
 * <p>
 * Implementations may return a fixed number of days or compute the duration
 * based on configuration settings or media-specific rules.
 * </p>
 */
public interface BorrowDurationStrategy {

    /**
     * Returns the number of days a media item may be borrowed.
     *
     * @return the loan duration in days
     */
    int getBorrowDays();
}

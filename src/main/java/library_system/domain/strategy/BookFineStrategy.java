package library_system.domain.strategy;

/**
 * Fine calculation strategy for books.
 * <p>
 * This implementation applies a fixed fine of 10 NIS
 * per overdue day for borrowed books. The value is constant and does
 * not depend on any external configuration.
 * </p>
 */
public class BookFineStrategy implements FineStrategy {

    /**
     * Returns the daily fine amount for overdue books.
     *
     * @return a fixed value of {@code 10} representing the fine per overdue day
     */
    @Override
    public int getDailyFine() {
        return 10;
    }
}

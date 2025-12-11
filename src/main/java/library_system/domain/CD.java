package library_system.domain;

import library_system.domain.strategy.BorrowDurationStrategy;
import library_system.domain.strategy.FineStrategy;

/**
 * Represents a CD (compact disc) in the library system.
 * <p>
 * A {@code CD} is a type of {@link Media} that uses configurable strategies
 * to determine loan duration and overdue fine calculations. Unlike books,
 * CDs generally have shorter loan periods or different fine structures,
 * depending on how the system is configured.
 * </p>
 */
public class CD extends Media {

    private BorrowDurationStrategy borrowStrategy;
    private FineStrategy fineStrategy;

    /**
     * Creates a new CD instance.
     *
     * @param id              unique identifier of the CD (e.g., CD code)
     * @param title           the title of the CD; must not be {@code null}
     * @param borrowStrategy  strategy defining how many days the CD may be borrowed
     * @param fineStrategy    strategy defining the fine charged per overdue day
     */
    public CD(String id, String title,
              BorrowDurationStrategy borrowStrategy,
              FineStrategy fineStrategy) {
        super(id, title);
        this.borrowStrategy = borrowStrategy;
        this.fineStrategy = fineStrategy;
    }

    /**
     * Returns the number of days this CD may be borrowed,
     * as defined by its borrowing strategy.
     *
     * @return the allowed borrowing duration in days
     */
    @Override
    public int getBorrowDays() {
        return borrowStrategy.getBorrowDays();
    }

    /**
     * Returns the fine amount charged per overdue day,
     * as defined by the fine strategy.
     *
     * @return the daily overdue fine
     */
    @Override
    public int getDailyFine() {
        return fineStrategy.getDailyFine();
    }
}

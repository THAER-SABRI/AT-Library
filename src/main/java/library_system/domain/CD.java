package library_system.domain;

import library_system.domain.strategy.BorrowDurationStrategy;
import library_system.domain.strategy.FineStrategy;

public class CD extends Media {

    private BorrowDurationStrategy borrowStrategy;
    private FineStrategy fineStrategy;

    public CD(String id, String title,
              BorrowDurationStrategy borrowStrategy,
              FineStrategy fineStrategy) {
        super(id, title);
        this.borrowStrategy = borrowStrategy;
        this.fineStrategy = fineStrategy;
    }

    @Override
    public int getBorrowDays() {
        return borrowStrategy.getBorrowDays();
    }

    @Override
    public int getDailyFine() {
        return fineStrategy.getDailyFine();
    }
}

package library_system.domain.strategy;

public class BookFineStrategy implements FineStrategy {
    @Override
    public int getDailyFine() {
        return 10;
    }
}

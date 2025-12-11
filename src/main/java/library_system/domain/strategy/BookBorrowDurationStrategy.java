package library_system.domain.strategy;

import library_system.application.SettingsGateway;

public class BookBorrowDurationStrategy implements BorrowDurationStrategy {

    private final SettingsGateway settings;

    public BookBorrowDurationStrategy(SettingsGateway settings) {
        this.settings = settings;
    }

    @Override
    public int getBorrowDays() {
        return settings.getLoanDays();
    }
}

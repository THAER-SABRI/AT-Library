package library_system.domain.strategy;

import library_system.application.SettingsGateway;

public class CdBorrowDurationStrategy implements BorrowDurationStrategy {

    private final SettingsGateway settings;

    public CdBorrowDurationStrategy(SettingsGateway settings) {
        this.settings = settings;
    }

    @Override
    public int getBorrowDays() {
        return settings.getCdLoanDays();
    }
}

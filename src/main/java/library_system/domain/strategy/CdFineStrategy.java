package library_system.domain.strategy;

import library_system.application.SettingsGateway;

public class CdFineStrategy implements FineStrategy {

    @Override
    public int getDailyFine() {
        return 20;
    }
}

package FakeImplementationForNeededInterfaces;

import library_system.application.SettingsGateway;

public class FakeSettings implements SettingsGateway {
    private int days = 28;

    @Override
    public int getLoanDays() { return days; }

    @Override
    public void setLoanDays(int days) { this.days = days; }
}
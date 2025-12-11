package FakeImplementationForNeededInterfaces;

import library_system.application.SettingsGateway;

public class FakeSettings implements SettingsGateway {

    public int loanDays = 5;
    public int cdLoanDays = 7;

    @Override
    public int getLoanDays() {
        return loanDays;
    }

    @Override
    public void setLoanDays(int days) {
        loanDays = days;
    }

    @Override
    public int getCdLoanDays() {
        return cdLoanDays;
    }

    @Override
    public void setCdLoanDays(int days) {
        cdLoanDays = days;
    }
}

package FakeImplementationForNeededInterfaces;
import library_system.application.SettingsGateway;

public class FakeSettings implements SettingsGateway {
    public int loanDays = 5;
    public int getLoanDays() { return loanDays; }
    public void setLoanDays(int d) { loanDays = d; }
}

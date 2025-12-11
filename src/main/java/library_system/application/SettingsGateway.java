package library_system.application;

public interface SettingsGateway {

    int getLoanDays();
    void setLoanDays(int days);

    int getCdLoanDays();
    void setCdLoanDays(int days);

}

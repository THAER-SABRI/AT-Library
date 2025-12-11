package library_system.application;

/**
 * Gateway interface responsible for accessing and modifying
 * library configuration settings such as loan durations.
 * <p>
 * Implementations of this interface may store settings in files,
 * databases, or other persistent storage systems.
 * </p>
 */
public interface SettingsGateway {

    /**
     * Retrieves the loan duration (in days) for books.
     *
     * @return the number of days a book may be borrowed
     */
    int getLoanDays();

    /**
     * Updates the loan duration (in days) for books.
     *
     * @param days the new loan period; should be a positive number
     */
    void setLoanDays(int days);

    /**
     * Retrieves the loan duration (in days) for CDs.
     *
     * @return the number of days a CD may be borrowed
     */
    int getCdLoanDays();

    /**
     * Updates the loan duration (in days) for CDs.
     *
     * @param days the new loan period; should be a positive number
     */
    void setCdLoanDays(int days);

}

package library_system.domain;

import java.util.Objects;

/**
 * Represents an administrative user in the library system.
 * <p>
 * An {@code Admin} is responsible for performing privileged actions
 * such as unregistering users or modifying system settings.
 * This class handles authentication (login/logout) and maintains
 * the admin's login state.
 * </p>
 */
public class Admin {

    private final String username;
    private final String password;
    private boolean loggedIn;

    /**
     * Creates a new admin account.
     *
     * @param username the admin's username; must not be {@code null} or empty
     * @param password the admin's password; must not be {@code null}
     *
     * @throws IllegalArgumentException if the username is blank or password is {@code null}
     */
    public Admin(String username, String password) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("username cannot be blank");
        if (password == null)
            throw new IllegalArgumentException("password cannot be null");

        this.username = username.trim();
        this.password = password;
        this.loggedIn = false;
    }

    /**
     * Attempts to log the admin into the system.
     *
     * @param u the provided username; must not be {@code null}
     * @param p the provided password; must not be {@code null}
     *
     * @return {@code true} if authentication succeeds and the admin is logged in;
     *         {@code false} otherwise
     */
    public boolean login(String u, String p) {
        if (u == null || p == null) return false;

        if (this.username.equalsIgnoreCase(u.trim()) && Objects.equals(this.password, p)) {
            loggedIn = true;
            return true;
        }
        return false;
    }

    /**
     * Logs the admin out of the system.
     * The admin must log in again to perform privileged operations.
     */
    public void logout() {
        loggedIn = false;
    }

    /**
     * Checks whether the admin is currently logged in.
     *
     * @return {@code true} if logged in; {@code false} otherwise
     */
    public boolean isLoggedIn() {
        return loggedIn;
    }

    /**
     * Returns the admin's username.
     *
     * @return the username associated with this admin account
     */
    public String getUsername() {
        return username;
    }
}

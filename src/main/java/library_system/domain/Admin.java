package library_system.domain;

import java.util.Objects;

public class Admin {
    private final String username;
    private final String password;
    private boolean loggedIn;

    public Admin(String username, String password) {
        if (username == null || username.trim().isEmpty())
            throw new IllegalArgumentException("username cannot be blank");
        if (password == null)
            throw new IllegalArgumentException("password cannot be null");
        this.username = username.trim();
        this.password = password;
        this.loggedIn = false;
    }

    public boolean login(String u, String p) {
        if (u == null || p == null) return false;
        if (this.username.equalsIgnoreCase(u.trim()) && Objects.equals(this.password, p)) {
            loggedIn = true;
            return true;
        }
        return false;
    }

    public void logout() {
        loggedIn = false;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public String getUsername() {
        return username;
    }
}

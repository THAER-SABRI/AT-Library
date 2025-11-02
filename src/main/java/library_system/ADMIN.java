package library_system;

public class ADMIN {
    private String username;
    private String password;
    private boolean loggedIn = false;

    public ADMIN(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean login(String username, String password) {
        if(username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty())
            return false;
        if (this.username.equals(username.trim()) && this.password.equals(password.trim())) {
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
}

package library_system;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ADMIN_TEST {
    private ADMIN admin;

    @BeforeEach
    void setup() {
        admin = new ADMIN("THAER", "777");
    }

    @Test
    void loginCorrect() {
        assertTrue(admin.login("THAER", "777"));
        assertTrue(admin.isLoggedIn());
    }

    @Test
    void loginWrong() {
        assertFalse(admin.login("THAER", "wrong"));
        assertFalse(admin.isLoggedIn());
    }

    @Test
    void loginEmptyOrNull() {
        assertFalse(admin.login("", "777"));
        assertFalse(admin.login("THAER", ""));
        assertFalse(admin.login(null, "777"));
        assertFalse(admin.login("THAER", null));
    }

    @Test
    void logout() {
        admin.login("THAER", "777");
        admin.logout();
        assertFalse(admin.isLoggedIn());
    }
}

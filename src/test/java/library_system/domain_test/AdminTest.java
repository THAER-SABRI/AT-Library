package library_system.domain_test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import library_system.domain.Admin;

import static org.junit.jupiter.api.Assertions.*;

class AdminTest {

    private Admin admin;

    @BeforeEach
    void setup() {
        admin = new Admin("THAER", "777");
    }

    @Test
    void loginCorrect() {
        assertTrue(admin.login("THAER", "777"));
        assertTrue(admin.isLoggedIn());
    }

    @Test
    void loginWrongPassword() {
        assertFalse(admin.login("THAER", "wrong"));
        assertFalse(admin.isLoggedIn());
    }

    @Test
    void loginWrongUsername() {
        assertFalse(admin.login("OtherUser", "777"));
        assertFalse(admin.isLoggedIn());
    }

    @Test
    void loginNullOrBlankInputs() {
        assertFalse(admin.login(null, "777"));
        assertFalse(admin.login("THAER", null));
        assertFalse(admin.login(null, null));
        assertFalse(admin.login("   ", "777"));
        assertFalse(admin.login("THAER", ""));
    }

    @Test
    void loginIsCaseInsensitiveForUsername() {
        assertTrue(admin.login("thaer", "777"));
        assertTrue(admin.isLoggedIn());
    }

    @Test
    void logoutWorks() {
        admin.login("THAER", "777");
        assertTrue(admin.isLoggedIn());

        admin.logout();
        assertFalse(admin.isLoggedIn());
    }
}


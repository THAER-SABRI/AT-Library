package library_system.infrastructure.persistence_test;

import library_system.infrastructure.persistence.FileUserDirectory;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FileUserDirectoryTest {

    @TempDir
    Path temp;

    FileUserDirectory dir;
    Path file;

    @BeforeEach
    void setup() {
        file = temp.resolve("USERS.TXT");
        dir = new FileUserDirectory(file.toString());
        dir.getAllUsers();  // IMPORTANT: forces header creation
    }

    @Test
    void headerIsCreatedWhenMissing() {
        List<String> users = dir.getAllUsers();
        assertEquals(1, users.size());
        assertTrue(users.get(0).contains("ID"));
    }

    @Test
    void addsUserSuccessfully() {
        dir.addUser("10", "Ali", "0599", "ali@mail.com");
        List<String> list = dir.getAllUsers();
        assertEquals(2, list.size());
        assertTrue(list.get(1).contains("10"));
        assertTrue(list.get(1).contains("Ali"));
    }

    @Test
    void preventsDuplicateUser() {
        dir.addUser("10", "Ali", "0599", "a@mail.com");
        dir.addUser("10", "Other", "0000", "other@mail.com");

        List<String> list = dir.getAllUsers();
        assertEquals(2, list.size());
    }

    @Test
    void ignoresInvalidId() {
        dir.addUser(null, "Ali", "0599", "x@mail.com");
        dir.addUser("   ", "Ali", "0599", "x@mail.com");

        List<String> list = dir.getAllUsers();
        assertEquals(1, list.size());
    }

    @Test
    void deleteAllRecreatesHeader() {
        dir.addUser("10", "Ali", "0599", "a@mail.com");
        dir.deleteAll();

        List<String> list = dir.getAllUsers();
        assertEquals(1, list.size());
        assertTrue(list.get(0).contains("ID"));
    }

    @Test
    void reloadCopiesAllLines() {
        dir.addUser("10", "Ali", "0599", "a@mail.com");

        List<String> target = new ArrayList<>();
        dir.reloadFromFile(target);

        assertEquals(2, target.size());
        assertTrue(target.get(1).contains("10"));
    }
}

package library_system.application_test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import FakeImplementationForNeededInterfaces.FakePaymentLedger;
import library_system.application.*;
import library_system.domain.Admin;
import library_system.domain.Book;

class UnregisterUserTest {

    private UserDirectory users;
    private BookRepository repo;
    private ComputeFine compute;
    private Admin admin;
    private UnregisterUser unregister;

    @BeforeEach
    void setup() {
        this.users = new FakeUsers();
        this.repo = new FakeBooks();

        FakePaymentLedger payment = new FakePaymentLedger();
        this.compute = new ComputeFine(repo, payment);

        this.admin = new Admin("THAER", "777");
        this.unregister = new UnregisterUser(users, repo, compute, admin);

        users.addUser("u1", "Name", "123", "mail");
    }

    @Test
    void cannotUnregisterIfAdminNotLoggedIn() {
        boolean result = unregister.execute("u1", LocalDate.now());
        assertFalse(result);
        assertEquals(1, users.getAllUsers().size());
    }

    @Test
    void unregisterWhenNoLoansAndNoFines() {
        admin.login("THAER", "777");
        boolean result = unregister.execute("u1", LocalDate.now());
        assertTrue(result);
        assertEquals(0, users.getAllUsers().size());
    }

    @Test
    void cannotUnregisterIfHasLoans() {
        admin.login("THAER", "777");

        List<Book> list = repo.getAll();
        Book b = new Book("T", "A", "100");
        b.setBorrowed(true);
        b.setBorrowerId("u1");
        list.add(b);
        repo.saveAll(list);

        boolean result = unregister.execute("u1", LocalDate.now());
        assertFalse(result);
        assertEquals(1, users.getAllUsers().size());
    }

    @Test
    void cannotUnregisterIfHasFines() {
        admin.login("THAER", "777");

        List<Book> list = repo.getAll();
        Book b = new Book("T", "A", "100");
        b.setBorrowed(true);
        b.setBorrowerId("u1");
        b.setDueDate(LocalDate.now().minusDays(5));
        list.add(b);
        repo.saveAll(list);

        boolean result = unregister.execute("u1", LocalDate.now());
        assertFalse(result);
        assertEquals(1, users.getAllUsers().size());
    }



    static class FakeUsers implements UserDirectory {
        List<String> list = new ArrayList<>();

        @Override
        public List<String> getAllUsers() {
            return list;
        }

        @Override
        public void addUser(String id, String name, String phone, String email) {
            list.add(id);
        }

        @Override
        public boolean removeUser(String id) {
            return list.remove(id);
        }
    }

    static class FakeBooks implements BookRepository {
        List<Book> list = new ArrayList<>();

        @Override
        public List<Book> getAll() {
            return list;
        }

        @Override
        public void saveAll(List<Book> books) {
            this.list = books;
        }
    }
}

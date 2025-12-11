package library_system.application_test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import FakeImplementationForNeededInterfaces.FakeBookRepo;
import FakeImplementationForNeededInterfaces.FakePaymentLedger;
import library_system.application.*;
import library_system.domain.Admin;
import library_system.domain.Book;
import library_system.domain.CD;

class UnregisterUserTest {

    private UserDirectory users;
    private BookRepository bookRepo;
    private CdRepository cdRepo;
    private ComputeFine compute;
    private Admin admin;
    private UnregisterUser unregister;

    @BeforeEach
    void setup() {
        users = new FakeUsers();
        bookRepo = new FakeBookRepo();
        cdRepo = new FakeCds();

        FakePaymentLedger payment = new FakePaymentLedger();
        compute = new ComputeFine(bookRepo, cdRepo, payment);

        admin = new Admin("THAER", "777");
        unregister = new UnregisterUser(users, bookRepo, compute, admin);

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

        List<Book> list = bookRepo.getAll();
        Book b = new Book("T", "A", "100", null, null);
        b.setBorrowed(true);
        b.setBorrowerId("u1");
        list.add(b);
        bookRepo.saveAll(list);

        boolean result = unregister.execute("u1", LocalDate.now());
        assertFalse(result);
        assertEquals(1, users.getAllUsers().size());
    }

    @Test
    void cannotUnregisterIfHasFines() {
        admin.login("THAER", "777");

        List<Book> list = bookRepo.getAll();
        Book b = new Book("T", "A", "100", null, null);
        b.setBorrowed(true);
        b.setBorrowerId("u1");
        b.setDueDate(LocalDate.now().minusDays(5));
        list.add(b);
        bookRepo.saveAll(list);

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

        @Override
        public String getEmail(String userId) {
            return "fake@mail.com";  // test does NOT use email, so return anything
        }
    }

    static class FakeCds implements CdRepository {
        List<CD> list = new ArrayList<>();

        @Override
        public List<CD> getAll() {
            return list;
        }

        @Override
        public void saveAll(List<CD> cds) {
            list = cds;
        }
    }
}

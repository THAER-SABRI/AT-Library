package library_system.application_test;

import FakeImplementationForNeededInterfaces.*;
import library_system.application.ReturnBook;
import library_system.application.ComputeFine;
import library_system.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReturnBookTest {

    private FakeBookRepo repo;
    private FakeBorrowLedger ledger;
    private ComputeFine compute;
    private ReturnBook service;

    @BeforeEach
    void setup() {
        repo = new FakeBookRepo();
        ledger = new FakeBorrowLedger();
        FakePaymentLedger payment = new FakePaymentLedger();
        compute = new ComputeFine(repo, payment);
        service = new ReturnBook(repo, ledger, compute);

        Book b = new Book("Book1", "Author", "111");
        b.setBorrowed(true);
        b.setBorrowerId("ALI");
        b.setDueDate(LocalDate.now().plusDays(5));
        repo.books.add(b);
    }

    @Test
    void returnBookSuccessfully() {
        boolean result = service.returnBook("111","ALI", LocalDate.now());

        assertTrue(result);

        Book b = repo.books.get(0);
        assertFalse(b.isBorrowed());
        assertNull(b.getBorrowerId());
        assertNull(b.getDueDate());

        assertEquals(1, ledger.logs.size());
        assertEquals("RETURN|111|ALI", ledger.logs.get(0));
    }

    @Test
    void cannotReturnNonBorrowedBook() {
        Book b2 = new Book("Book2", "X", "222");
        repo.books.add(b2);

        boolean result = service.returnBook("222","ALI", LocalDate.now());

        assertFalse(result);
        assertTrue(ledger.logs.isEmpty());
    }

    @Test
    void cannotReturnInvalidISBN() {
        assertFalse(service.returnBook(null,"ALI", LocalDate.now()));
        assertFalse(service.returnBook("","ALI", LocalDate.now()));
        assertFalse(service.returnBook("   ","ALI", LocalDate.now()));
        assertTrue(ledger.logs.isEmpty());
    }

    @Test
    void cannotReturnIfISBNNotFound() {
        boolean result = service.returnBook("999","ALI", LocalDate.now());
        assertFalse(result);
        assertTrue(ledger.logs.isEmpty());
    }
}

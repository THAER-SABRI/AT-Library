package library_system.application_test;

import library_system.application.PayFine;
import library_system.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import FakeImplementationForNeededInterfaces.FakeBookRepo;
import FakeImplementationForNeededInterfaces.FakePaymentLedger;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PayFineTest {

    private FakePaymentLedger ledger;
    private FakeBookRepo repo;
    private PayFine service;

    @BeforeEach
    void setup() {
        ledger = new FakePaymentLedger();
        repo = new FakeBookRepo();
        service = new PayFine(ledger, repo);
    }

    @Test
    void payFullFine() {
        Book b = new Book("B1", "A", "111");
        b.borrow("ALI", LocalDate.now().minusDays(4));
        repo.books.add(b);

        double remaining = service.pay("ALI", 4 * 0.5, LocalDate.now());

        assertEquals(0.0, remaining);
        assertEquals(1, ledger.logs.size());
    }

    @Test
    void payPartialFine() {
        Book b = new Book("B2", "A", "222");
        b.borrow("ALI", LocalDate.now().minusDays(6));
        repo.books.add(b);

        double remaining = service.pay("ALI", 2.0, LocalDate.now());

        assertEquals(6 * 0.5 - 2.0, remaining);
        assertEquals(1, ledger.logs.size());
    }

    @Test
    void payZeroAmountReturnsFullFine() {
        Book b = new Book("B3", "A", "333");
        b.borrow("ALI", LocalDate.now().minusDays(3));
        repo.books.add(b);

        double remaining = service.pay("ALI", 0.0, LocalDate.now());

        assertEquals(3 * 0.5, remaining);
        assertEquals(0, ledger.logs.size());
    }

    @Test
    void noFineIfNotOverdue() {
        Book b = new Book("B4", "A", "444");
        b.borrow("ALI", LocalDate.now().plusDays(5));
        repo.books.add(b);

        double remaining = service.pay("ALI", 10.0, LocalDate.now());

        assertEquals(0.0, remaining);
        assertEquals(1, ledger.logs.size());
    }

    @Test
    void invalidInputsReturnZero() {
        double r1 = service.pay(null, 10.0, LocalDate.now());
        double r2 = service.pay("   ", 10.0, LocalDate.now());
        double r3 = service.pay("ALI", 10.0, null);

        assertEquals(0.0, r1);
        assertEquals(0.0, r2);
        assertEquals(0.0, r3);
        assertEquals(0, ledger.logs.size());
    }
}

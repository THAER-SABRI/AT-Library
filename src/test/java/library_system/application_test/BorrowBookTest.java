package library_system.application_test;

import FakeImplementationForNeededInterfaces.*;
import library_system.application.*;
import library_system.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BorrowBookTest {

    FakeBookRepo repo;
    FakeBorrowLedger ledger;
    FakeSettings settings;
    FakeOverdueBooks overdue;
    BorrowBook service;

    @BeforeEach
    void setup() {
        repo = new FakeBookRepo();
        ledger = new FakeBorrowLedger();
        settings = new FakeSettings();
        settings.loanDays = 5;
        overdue = new FakeOverdueBooks();

        service = new BorrowBook(repo, ledger, settings, overdue);

        repo.books.add(new Book("B1", "A", "111"));
        repo.books.add(new Book("B2", "A", "222"));
    }

    @Test
    void borrowSuccess() {
        boolean result = service.borrow("111", "ALI", LocalDate.now());
        assertTrue(result);
        assertTrue(repo.books.get(0).isBorrowed());
        assertEquals(1, ledger.logs.size());
    }

    @Test
    void cannotBorrowAlreadyBorrowed() {
        repo.books.get(0).borrow("X", LocalDate.now().plusDays(3));
        boolean result = service.borrow("111", "ALI", LocalDate.now());
        assertFalse(result);
        assertTrue(ledger.logs.isEmpty());
    }

    @Test
    void cannotBorrowIfUserHasOverdue() {
        Book overdueBook = new Book("OB", "AA", "999");
        overdueBook.borrow("ALI", LocalDate.now().minusDays(2));
        overdue.overdue.add(overdueBook);

        boolean result = service.borrow("111", "ALI", LocalDate.now());
        assertFalse(result);
        assertTrue(ledger.logs.isEmpty());
    }

    @Test
    void invalidISBN() {
        assertFalse(service.borrow(null, "ALI", LocalDate.now()));
        assertFalse(service.borrow("", "ALI", LocalDate.now()));
        assertFalse(service.borrow("   ", "ALI", LocalDate.now()));
        assertTrue(ledger.logs.isEmpty());
    }

    @Test
    void invalidUser() {
        assertFalse(service.borrow("111", null, LocalDate.now()));
        assertFalse(service.borrow("111", "", LocalDate.now()));
        assertFalse(service.borrow("111", "   ", LocalDate.now()));
        assertTrue(ledger.logs.isEmpty());
    }

    @Test
    void invalidDate() {
        assertFalse(service.borrow("111", "ALI", null));
        assertTrue(ledger.logs.isEmpty());
    }
}

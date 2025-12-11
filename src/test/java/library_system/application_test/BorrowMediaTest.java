package library_system.application_test;

import FakeImplementationForNeededInterfaces.*;
import library_system.application.*;
import library_system.domain.Book;
import library_system.domain.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BorrowMediaTest {

    FakeBookRepo bookRepo;
    FakeCdRepo cdRepo;
    FakeBorrowLedger ledger;
    FakeOverdueMedia overdue;
    BorrowMedia service;

    BorrowDurationStrategy fiveDays = new BookBorrowDurationStrategy(new FakeSettings());
    FineStrategy tenFine = new BookFineStrategy();

    @BeforeEach
    void setup() {
        bookRepo = new FakeBookRepo();
        cdRepo = new FakeCdRepo();
        ledger = new FakeBorrowLedger();
        overdue = new FakeOverdueMedia();

        service = new BorrowMedia(bookRepo, cdRepo, ledger, overdue);

        bookRepo.books.add(new Book("B1", "A", "111", fiveDays, tenFine));
        bookRepo.books.add(new Book("B2", "A", "222", fiveDays, tenFine));
    }

    @Test
    void borrowSuccess() {
        boolean result = service.borrow("111", "ALI", LocalDate.now());
        assertTrue(result);

        assertTrue(
            bookRepo.books.stream()
                .anyMatch(b -> b.getIsbn().equals("111") && b.isBorrowed())
        );

        assertEquals(1, ledger.logs.size());
    }


    @Test
    void cannotBorrowAlreadyBorrowed() {
        bookRepo.books.get(0).borrow("X", LocalDate.now().plusDays(3));
        boolean result = service.borrow("111", "ALI", LocalDate.now());
        assertFalse(result);
        assertTrue(ledger.logs.isEmpty());
    }

    @Test
    void cannotBorrowIfUserHasOverdue() {
        Book ob = new Book("OB", "AA", "999", fiveDays, tenFine);
        ob.borrow("ALI", LocalDate.now().minusDays(2));
        overdue.overdue.add(ob);

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

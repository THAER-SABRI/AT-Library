package library_system.application_test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import library_system.application.*;
import library_system.domain.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import FakeImplementationForNeededInterfaces.FakeBookRepo;
import FakeImplementationForNeededInterfaces.FakeBorrowLedger;
import FakeImplementationForNeededInterfaces.FakeSettings;

import static org.junit.jupiter.api.Assertions.*;





public class BorrowBookTest {

	private FakeBookRepo repo;
    private FakeBorrowLedger ledger;
    private FakeSettings settings;
    private BorrowBook service;

    @BeforeEach
    void setup() {
        repo = new FakeBookRepo();
        repo.books.clear();
        ledger = new FakeBorrowLedger();
        settings = new FakeSettings();
        service = new BorrowBook(repo, ledger, settings);

        repo.books.add(new Book("BOOK1", "A", "111"));
    }

    @Test
    void borrowBookSuccessfully() {
        LocalDate today = LocalDate.of(2024, 1, 1);

        boolean result = service.borrow("111", "20", today);

        assertTrue(result);
        Book b = repo.getAll().get(0);
        assertTrue(b.isBorrowed());
        assertEquals("20", b.getBorrowerId());
        assertEquals(today.plusDays(28), b.getDueDate());

        assertEquals(1, ledger.logs.size());
        assertTrue(ledger.logs.get(0).startsWith("BORROW|111|20|"));

    }

    @Test
    void cannotBorrowMissingBook() {
        repo.books.clear();

        boolean result = service.borrow("111", "10", LocalDate.now());

        assertFalse(result);
        assertEquals(0, ledger.logs.size());
    }

    @Test
    void cannotBorrowAlreadyBorrowedBook() {
        Book b = repo.books.get(0);
        b.borrow("OLDUSER", LocalDate.now().plusDays(10));

        boolean result = service.borrow("111", "10", LocalDate.now());

        assertFalse(result); 
        assertEquals("OLDUSER", b.getBorrowerId());
        assertEquals(0, ledger.logs.size());
    }

    @Test
    void invalidInputsReturnFalse() {
        assertFalse(service.borrow(null, "20", LocalDate.now()));
        assertFalse(service.borrow("111", null, LocalDate.now()));
        assertFalse(service.borrow("111", "20", null));
        assertFalse(service.borrow("   ", "20", LocalDate.now()));
        assertFalse(service.borrow("111", "   ", LocalDate.now()));

        assertEquals(0, ledger.logs.size());
    }
}

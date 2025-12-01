package library_system.application_test;

import library_system.application.OverdueBooks;
import library_system.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import FakeImplementationForNeededInterfaces.FakeBookRepo;
import FakeImplementationForNeededInterfaces.FakeBorrowLedger;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class OverdueBooksTest {

    private FakeBookRepo repo;
    private FakeBorrowLedger ledger;
    private OverdueBooks service;

    @BeforeEach
    void setup() {
        repo = new FakeBookRepo();
        ledger = new FakeBorrowLedger();
        service = new OverdueBooks(repo, ledger);
    }

    @Test
    void overdueBooksDetected() {
        Book b = new Book("Book1", "A", "111");
        repo.books.add(b);

        ledger.logs.add("| BORROW | 111 | ALI | " + LocalDate.now().minusDays(5) + " |");

        List<Book> result = service.getOverdueBooks(LocalDate.now());

        assertEquals(1, result.size());
        assertEquals("111", result.get(0).getIsbn());
    }

    @Test
    void nonOverdueBooksIgnored() {
        Book b = new Book("Book2", "B", "222");
        repo.books.add(b);

        ledger.logs.add("| BORROW | 222 | ALI | " + LocalDate.now().plusDays(3) + " |");

        List<Book> result = service.getOverdueBooks(LocalDate.now());

        assertTrue(result.isEmpty());
    }

    @Test
    void malformedLinesIgnored() {
        Book b = new Book("Book3", "C", "333");
        repo.books.add(b);

        ledger.logs.add("BROKEN LINE WITHOUT FIELDS");
        ledger.logs.add("| BORROW | 333 | ALI | not-a-date |");

        List<Book> result = service.getOverdueBooks(LocalDate.now());

        assertTrue(result.isEmpty());
    }

    @Test
    void wrongTypeLinesIgnored() {
        Book b = new Book("Book4", "D", "444");
        repo.books.add(b);

        ledger.logs.add("| RETURN | 444 | ALI | 2024-01-01 |");

        List<Book> result = service.getOverdueBooks(LocalDate.now());

        assertTrue(result.isEmpty());
    }

    @Test
    void missingBookIgnored() {
        ledger.logs.add("| BORROW | 555 | ALI | " + LocalDate.now().minusDays(10) + " |");

        List<Book> result = service.getOverdueBooks(LocalDate.now());

        assertTrue(result.isEmpty());
    }

    @Test
    void emptyLedgerReturnsNoOverdues() {
        Book b = new Book("B", "X", "999");
        repo.books.add(b);

        List<Book> result = service.getOverdueBooks(LocalDate.now());

        assertTrue(result.isEmpty());
    }
}
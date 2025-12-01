package library_system.application_test;

import FakeImplementationForNeededInterfaces.*;
import library_system.application.ReturnBook;
import library_system.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;



class ReturnBookTest {

    private FakeBookRepo repo;
    private FakeBorrowLedger ledger;
    private ReturnBook service;

    @BeforeEach
    void setup() {
        repo = new FakeBookRepo();
        ledger = new FakeBorrowLedger();
        service = new ReturnBook(repo, ledger);

        Book b = new Book("Book1", "Author", "111");
        b.borrow("ALI", java.time.LocalDate.now().plusDays(5));
        repo.books.add(b);
    }

    @Test
    void returnBookSuccessfully() {
        boolean result = service.returnBook("111");

        assertTrue(result);

        Book b = repo.books.get(0);
        assertFalse(b.isBorrowed());
        assertNull(b.getBorrowerId());
        assertNull(b.getDueDate());

        assertEquals(1, ledger.logs.size());
        assertEquals("RETURN|111", ledger.logs.get(0));
    }

    @Test
    void cannotReturnNonBorrowedBook() {
        Book b2 = new Book("Book2", "X", "222");
        repo.books.add(b2);

        boolean result = service.returnBook("222");

        assertFalse(result);
        assertTrue(ledger.logs.isEmpty());
    }

    @Test
    void cannotReturnInvalidISBN() {
        assertFalse(service.returnBook(null));
        assertFalse(service.returnBook(""));
        assertFalse(service.returnBook("   "));
        assertTrue(ledger.logs.isEmpty());
    }

    @Test
    void cannotReturnIfISBNNotFound() {
        boolean result = service.returnBook("999");
        assertFalse(result);
        assertTrue(ledger.logs.isEmpty());
    }
}

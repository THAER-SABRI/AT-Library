package library_system.application_test;

import FakeImplementationForNeededInterfaces.*;
import library_system.application.*;
import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ReturnMediaTest {

    FakeBookRepo bookRepo;
    FakeCdRepo cdRepo;
    FakeBorrowLedger ledger;
    FakeOverdueMedia overdue;
    FakeDispatcher dispatcher;
    ReturnMedia service;

    BorrowDurationStrategy fiveDays = new BookBorrowDurationStrategy(new FakeSettings());
    FineStrategy fine = new BookFineStrategy();

    @BeforeEach
    void setup() {
        bookRepo = new FakeBookRepo();
        cdRepo = new FakeCdRepo();
        ledger = new FakeBorrowLedger();
        overdue = new FakeOverdueMedia();
        dispatcher = new FakeDispatcher();

        service = new ReturnMedia(
                bookRepo,
                cdRepo,
                ledger,
                new ComputeFine(bookRepo, cdRepo, new FakePaymentLedger()),
                dispatcher
        );

        bookRepo.books.add(new Book("B1", "A", "111", fiveDays, fine));
        bookRepo.books.add(new Book("B2", "A", "222", fiveDays, fine));

        cdRepo.cds.add(new CD("1", "CD1", fiveDays, fine));
        cdRepo.cds.add(new CD("2", "CD2", fiveDays, fine));
    }

    @Test
    void returnBookSuccess() {
        Book b = bookRepo.books.get(0);
        b.borrow("ALI", LocalDate.now().plusDays(2));

        boolean result = service.returnMedia(1, "111", "ALI", LocalDate.now());
        assertTrue(result);

        assertFalse(b.isBorrowed());
        assertEquals(1, ledger.logs.size());
    }

    @Test
    void returnCdSuccess() {
        CD cd = cdRepo.cds.get(0);
        cd.borrow("ALI", LocalDate.now().plusDays(2));

        boolean result = service.returnMedia(2, "1", "ALI", LocalDate.now());
        assertTrue(result);

        assertFalse(cd.isBorrowed());
        assertEquals(1, ledger.logs.size());
    }

    @Test
    void cannotReturnIfNotBorrowed() {
        boolean result = service.returnMedia(1, "111", "ALI", LocalDate.now());
        assertFalse(result);
        assertTrue(ledger.logs.isEmpty());
    }

    @Test
    void cannotReturnIfWrongUser() {
        Book b = bookRepo.books.get(0);
        b.borrow("OTHER", LocalDate.now().plusDays(2));

        boolean result = service.returnMedia(1, "111", "ALI", LocalDate.now());
        assertFalse(result);
        assertTrue(ledger.logs.isEmpty());
    }

    @Test
    void cannotReturnWithOutstandingFines() {
        Book b = bookRepo.books.get(0);
        b.borrow("ALI", LocalDate.now().minusDays(5));
        overdue.overdue.add(b);

        boolean result = service.returnMedia(1, "111", "ALI", LocalDate.now());
        assertFalse(result);
        assertTrue(ledger.logs.isEmpty());
    }

    @Test
    void invalidInputs() {
        assertFalse(service.returnMedia(1, null, "ALI", LocalDate.now()));
        assertFalse(service.returnMedia(1, "  ", "ALI", LocalDate.now()));
        assertFalse(service.returnMedia(1, "111", null, LocalDate.now()));
        assertFalse(service.returnMedia(1, "111", "  ", LocalDate.now()));
        assertFalse(service.returnMedia(1, "111", "ALI", null));
        assertTrue(ledger.logs.isEmpty());
    }
}

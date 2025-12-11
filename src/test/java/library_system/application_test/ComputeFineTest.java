package library_system.application_test;

import FakeImplementationForNeededInterfaces.*;
import library_system.application.*;
import library_system.domain.Book;
import library_system.domain.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class ComputeFineTest {

    FakeBookRepo bookRepo;
    FakeCdRepo cdRepo;
    FakePaymentLedger payment;
    ComputeFine computeFine;

    BorrowDurationStrategy borrowDays = new BookBorrowDurationStrategy(new FakeSettings());
    FineStrategy bookFine = new BookFineStrategy();

    @BeforeEach
    void setup() {
        bookRepo = new FakeBookRepo();
        cdRepo = new FakeCdRepo();
        payment = new FakePaymentLedger();
        computeFine = new ComputeFine(bookRepo, cdRepo, payment);
    }

    @Test
    void overdueFineApplied() {
        Book b = new Book("111", "BOOK1", "THAER", borrowDays, bookFine);
        b.borrow("ALI", LocalDate.now().minusDays(10));
        bookRepo.books.add(b);

        double fine = computeFine.computeOutstanding("ALI", LocalDate.now());
        assertEquals(10 * 10, fine);
    }

    @Test
    void noFineIfNotOverdue() {
        Book b = new Book("222", "BOOK2", "HAYA", borrowDays, bookFine);
        b.borrow("ALI", LocalDate.now().plusDays(10));
        bookRepo.books.add(b);

        double fine = computeFine.computeOutstanding("ALI", LocalDate.now());
        assertEquals(0.0, fine);
    }

    @Test
    void fineIsZeroIfBookNotBorrowed() {
        Book b = new Book("333", "BOOK3", "THAER", borrowDays, bookFine);
        bookRepo.books.add(b);

        double fine = computeFine.computeOutstanding("ALI", LocalDate.now());
        assertEquals(0.0, fine);
    }

    @Test
    void multipleBooksOnlyOverdueCounted() {
        Book b1 = new Book("111", "B1", "A", borrowDays, bookFine);
        Book b2 = new Book("222", "B2", "B", borrowDays, bookFine);

        b1.borrow("ALI", LocalDate.now().minusDays(15));
        b2.borrow("ALI", LocalDate.now().plusDays(15));

        bookRepo.books.add(b1);
        bookRepo.books.add(b2);

        double fine = computeFine.computeOutstanding("ALI", LocalDate.now());
        assertEquals(15 * 10, fine);
    }

    @Test
    void fineIsZeroIfUserIdNullOrBlank() {
        Book b = new Book("111", "BOOK1", "THAER", borrowDays, bookFine);
        b.borrow("ALI", LocalDate.now().minusDays(5));
        bookRepo.books.add(b);

        assertEquals(0.0, computeFine.computeOutstanding(null, LocalDate.now()));
        assertEquals(0.0, computeFine.computeOutstanding("", LocalDate.now()));
        assertEquals(0.0, computeFine.computeOutstanding("   ", LocalDate.now()));
    }

    @Test
    void fineIsZeroIfDueDateNull() {
        Book b = new Book("999", "BOOK9", "A", borrowDays, bookFine);
        b.borrow("ALI", LocalDate.now().plusDays(5));
        b.returnBook();
        bookRepo.books.add(b);

        double fine = computeFine.computeOutstanding("ALI", LocalDate.now());
        assertEquals(0.0, fine);
    }
}

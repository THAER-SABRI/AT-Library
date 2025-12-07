package library_system.application_test;

import library_system.application.ComputeFine;
import library_system.domain.Book;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import FakeImplementationForNeededInterfaces.FakeBookRepo;
import FakeImplementationForNeededInterfaces.FakePaymentLedger;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class ComputeFineTest {

    private FakeBookRepo repo;
    private ComputeFine computeFine;

    @BeforeEach
    void setup() {
        repo = new FakeBookRepo();
        computeFine = new ComputeFine(repo, new FakePaymentLedger());
    }

    @Test
    void overdueFineApplied() {
        Book b = new Book("BOOK1", "THAER", "111");
        b.borrow("ALI", LocalDate.now().minusDays(10)); 
        repo.books.add(b);

        double fine = computeFine.computeOutstanding("ALI", LocalDate.now());
        assertEquals(10 * 0.5, fine);
    }

    @Test
    void noFineIfNotOverdue() {
        Book b = new Book("BOOK2", "HAYA", "222");
        b.borrow("ALI", LocalDate.now().plusDays(10)); 
        repo.books.add(b);

        double fine = computeFine.computeOutstanding("ALI", LocalDate.now());
        assertEquals(0.0, fine);
    }

    @Test
    void fineIsZeroIfBookNotBorrowed() {
        Book b = new Book("BOOK3", "THAER", "333"); 
        repo.books.add(b);

        double fine = computeFine.computeOutstanding("ALI", LocalDate.now());
        assertEquals(0.0, fine);
    }

    @Test
    void multipleBooksOnlyOverdueCounted() {
        Book b1 = new Book("B1", "A", "111");
        Book b2 = new Book("B2", "B", "222");

        b1.borrow("ALI", LocalDate.now().minusDays(15)); 
        b2.borrow("ALI", LocalDate.now().plusDays(15));  

        repo.books.add(b1);
        repo.books.add(b2);

        double fine = computeFine.computeOutstanding("ALI", LocalDate.now());
        assertEquals(15 * 0.5, fine);
    }

    @Test
    void fineIsZeroIfUserIdNullOrBlank() {
        Book b = new Book("BOOK1", "THAER", "111");
        b.borrow("ALI", LocalDate.now().minusDays(5));
        repo.books.add(b);

        assertEquals(0.0, computeFine.computeOutstanding(null, LocalDate.now()));
        assertEquals(0.0, computeFine.computeOutstanding("", LocalDate.now()));
        assertEquals(0.0, computeFine.computeOutstanding("   ", LocalDate.now()));
    }

    @Test
    void fineIsZeroIfDueDateNull() {
        Book b = new Book("BOOK9", "A", "999");
        b.borrow("ALI", LocalDate.now().plusDays(5));
        b.returnBook();
        repo.books.add(b);

        double fine = computeFine.computeOutstanding("ALI", LocalDate.now());
        assertEquals(0.0, fine);
    }
}

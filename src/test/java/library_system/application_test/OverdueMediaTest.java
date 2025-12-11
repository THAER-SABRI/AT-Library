package library_system.application_test;

import FakeImplementationForNeededInterfaces.*;
import library_system.application.*;
import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.Media;
import library_system.domain.strategy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OverdueMediaTest {

    FakeBookRepo bookRepo;
    FakeCdRepo cdRepo;
    OverdueMedia service;

    BorrowDurationStrategy days = new BookBorrowDurationStrategy(new FakeSettings());
    FineStrategy fine = new BookFineStrategy();

    @BeforeEach
    void setup() {
        bookRepo = new FakeBookRepo();
        cdRepo = new FakeCdRepo();
        service = new OverdueMedia(bookRepo, cdRepo);
    }

    @Test
    void overdueBookDetected() {
        Book b = new Book("B1", "A", "111", days, fine);
        b.borrow("ALI", LocalDate.now().minusDays(3));
        bookRepo.books.add(b);

        List<Media> result = service.getAllOverdues(LocalDate.now());
        assertEquals(1, result.size());
        assertEquals("111", result.get(0).getId());
    }

    @Test
    void nonOverdueBookIgnored() {
        Book b = new Book("B2", "A", "222", days, fine);
        b.borrow("ALI", LocalDate.now().plusDays(3));
        bookRepo.books.add(b);

        List<Media> result = service.getAllOverdues(LocalDate.now());
        assertTrue(result.isEmpty());
    }

    @Test
    void overdueCdDetected() {
        CD cd = new CD("CD1", "Music", new CdBorrowDurationStrategy(new FakeSettings()), new CdFineStrategy());
        cd.borrow("ALI", LocalDate.now().minusDays(2));
        cdRepo.cds.add(cd);

        List<Media> result = service.getAllOverdues(LocalDate.now());
        assertEquals(1, result.size());
        assertEquals("CD1", result.get(0).getId());
    }

    @Test
    void nonOverdueCdIgnored() {
        CD cd = new CD("CD2", "Album", new CdBorrowDurationStrategy(new FakeSettings()), new CdFineStrategy());
        cd.borrow("ALI", LocalDate.now().plusDays(4));
        cdRepo.cds.add(cd);

        List<Media> result = service.getAllOverdues(LocalDate.now());
        assertTrue(result.isEmpty());
    }

    @Test
    void missingDueDateIgnored() {
        Book b = new Book("B9", "A", "999", days, fine);
        bookRepo.books.add(b);

        List<Media> result = service.getAllOverdues(LocalDate.now());
        assertTrue(result.isEmpty());
    }

    @Test
    void overdueForSpecificUser() {
        Book b1 = new Book("B1", "A", "111", days, fine);
        b1.borrow("ALI", LocalDate.now().minusDays(3));

        Book b2 = new Book("B2", "A", "222", days, fine);
        b2.borrow("OMAR", LocalDate.now().minusDays(3));

        bookRepo.books.add(b1);
        bookRepo.books.add(b2);

        List<Media> r = service.getOverdueForUser("ALI", LocalDate.now());
        assertEquals(1, r.size());
        assertEquals("111", r.get(0).getId());
    }

    @Test
    void userHasOverduesTrue() {
        Book b = new Book("B7", "A", "777", days, fine);
        b.borrow("ALI", LocalDate.now().minusDays(5));
        bookRepo.books.add(b);

        assertTrue(service.userHasOverdues("ALI", LocalDate.now()));
    }

    @Test
    void userHasOverduesFalse() {
        Book b = new Book("B8", "A", "888", days, fine);
        b.borrow("ALI", LocalDate.now().plusDays(5));
        bookRepo.books.add(b);

        assertFalse(service.userHasOverdues("ALI", LocalDate.now()));
    }
}

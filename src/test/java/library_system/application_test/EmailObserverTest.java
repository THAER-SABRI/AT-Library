package library_system.application_test;

import library_system.application.BookRepository;
import library_system.application.CdRepository;
import library_system.application.EmailObserver;
import library_system.application.EmailService;
import library_system.application.UserDirectory;
import library_system.domain.Book;
import library_system.domain.events.BookBorrowedEvent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class EmailObserverTest {

    EmailService emailService;
    UserDirectory userDirectory;
    BookRepository bookRepo;
    CdRepository cdRepo;

    EmailObserver observer;

    @BeforeEach
    void setup() {
        emailService = mock(EmailService.class);
        userDirectory = mock(UserDirectory.class);
        bookRepo = mock(BookRepository.class);
        cdRepo = mock(CdRepository.class);

        Book b = new Book("Test Book", "Author", "111", null, null);
        b.borrow("1", LocalDate.now().plusDays(7));

        when(bookRepo.getAll()).thenReturn(Arrays.asList(b));
        when(userDirectory.getEmail("1")).thenReturn("omar@test.com");

        observer = new EmailObserver(emailService, userDirectory, bookRepo, cdRepo);
    }

    @Test
    void borrowEventSendsEmail() {
        BookBorrowedEvent event = new BookBorrowedEvent("1", "111");

        observer.onEvent(event);

        ArgumentCaptor<String> to = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> subject = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> body = ArgumentCaptor.forClass(String.class);

        verify(emailService, times(1)).sendEmail(to.capture(), subject.capture(), body.capture());

        assertEquals("omar@test.com", to.getValue());
        assertEquals("Book Borrowed", subject.getValue());

        String bodyText = body.getValue();
        assertTrue(bodyText.contains("Test Book"));
        assertTrue(bodyText.contains("ISBN: 111"));
        assertTrue(bodyText.contains("Due Date"));
        assertTrue(bodyText.contains("borrowed"));
    }
}

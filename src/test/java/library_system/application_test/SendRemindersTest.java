package library_system.application_test;

import library_system.application.SendReminders;
import library_system.application.ComputeFine;
import library_system.application.EmailService;
import FakeImplementationForNeededInterfaces.FakeBookRepo;
import FakeImplementationForNeededInterfaces.FakePaymentLedger;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.nio.file.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SendRemindersTest {

    private EmailService email;
    private ComputeFine computeFine;
    private SendReminders service;
    private FakeBookRepo repo;

    @BeforeEach
    void setup() throws Exception {
        Files.createDirectories(Paths.get("DATA"));

        Files.write(Paths.get("DATA/OVERDUE.TXT"),
                java.util.Arrays.asList(
                        "| TYPE | ISBN | USER | DUE |",
                        "| BORROW | 111 | U1 | " + LocalDate.now().minusDays(2) + " |"
                ));

        Files.write(Paths.get("DATA/USERS.TXT"),
                java.util.Arrays.asList(
                        "| ID | NAME | PHONE | EMAIL |",
                        "| U1 | X | 000 | u1@mail.com |"
                ));

        Files.write(Paths.get("DATA/REMINDERS.TXT"), new byte[0]);

        repo = new FakeBookRepo();
        library_system.domain.Book b = new library_system.domain.Book("B1", "A", "111");
        b.borrow("U1", LocalDate.now().minusDays(2));
        repo.books.add(b);

        FakePaymentLedger payment = new FakePaymentLedger();
        computeFine = new ComputeFine(repo, payment);

        email = Mockito.mock(EmailService.class);

        service = new SendReminders(email, computeFine);
    }

    @Test
    void sendsEmailToRealUser() {
        int count = service.sendAll(LocalDate.now());

        assertEquals(1, count);

        verify(email, times(1)).sendEmail(
                eq("u1@mail.com"),
                eq("Library Overdue Notice"),
                anyString()
        );

        assertEquals(1, service.getSentLogs().size());
    }

    @AfterEach
    void cleanup() throws Exception {
        Files.deleteIfExists(Paths.get("DATA/OVERDUE.TXT"));
        Files.deleteIfExists(Paths.get("DATA/USERS.TXT"));
        Files.deleteIfExists(Paths.get("DATA/REMINDERS.TXT"));
    }
}

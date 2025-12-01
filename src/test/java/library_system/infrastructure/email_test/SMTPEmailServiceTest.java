package library_system.infrastructure.email_test;

import library_system.infrastructure.email.SMTPEmailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Transport;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SMTPEmailServiceTest {

    @Test
    void sendEmailSuccessful() throws Exception {

        SMTPEmailService smtp = new SMTPEmailService(
                "user@mail.com",
                "pass",
                "smtp.test.com",
                587,
                true
        );

        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {

            mockedTransport.when(() -> Transport.send(any(Message.class)))
                    .thenAnswer(inv -> null);

            smtp.sendEmail("to@mail.com", "Subject", "Body");

            mockedTransport.verify(() -> Transport.send(any(Message.class)), times(1));
        }
    }

    @Test
    void sendEmailThrowsRuntimeWhenMessagingExceptionOccurs() throws Exception {

        SMTPEmailService smtp = new SMTPEmailService(
                "user@mail.com",
                "pass",
                "smtp.test.com",
                587,
                false
        );

        try (MockedStatic<Transport> mockedTransport = mockStatic(Transport.class)) {

            mockedTransport.when(() -> Transport.send(any(Message.class)))
                    .thenThrow(new MessagingException("SMTP failure"));

            assertThrows(RuntimeException.class, () ->
                    smtp.sendEmail("x@mail.com", "S", "B")
            );
        }
    }

    @Test
    void nullRecipientTriggersExceptionFromJavaMail() {

        SMTPEmailService smtp = new SMTPEmailService(
                "user@mail.com",
                "pass",
                "smtp.test.com",
                587,
                true
        );

        assertThrows(RuntimeException.class, () ->
                smtp.sendEmail(null, "S", "B")
        );
    }
}

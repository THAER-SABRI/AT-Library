package library_system.infrastructure.email_test;

import library_system.infrastructure.email.InMemoryEmailClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryEmailClientTest {

    private InMemoryEmailClient client;

    @BeforeEach
    void setup() {
        client = new InMemoryEmailClient();
    }

    @Test
    void sendValidEmailStoresMessage() {
        client.send("user@mail.com", "Hello there");

        assertEquals(1, client.getSentCount());
        assertEquals(1, client.getSentEmails().size());
        assertTrue(client.getSentEmails().get(0).contains("user@mail.com"));
        assertTrue(client.getSentEmails().get(0).contains("Hello there"));
    }

    @Test
    void sendingEmailWithNullToDoesNothing() {
        client.send(null, "Message");
        assertEquals(0, client.getSentCount());
    }

    @Test
    void sendingEmailWithEmptyToDoesNothing() {
        client.send("   ", "Message");
        assertEquals(0, client.getSentCount());
    }

    @Test
    void sendingEmailWithNullMessageDoesNothing() {
        client.send("user@mail.com", null);
        assertEquals(0, client.getSentCount());
    }

    @Test
    void getSentEmailsReturnsCopyNotReference() {
        client.send("a@mail.com", "Hi");
        var list1 = client.getSentEmails();
        list1.clear(); // should NOT affect internal list

        assertEquals(1, client.getSentCount());
    }

    @Test
    void clearRemovesAllMessages() {
        client.send("a@mail.com", "Hi");
        client.send("b@mail.com", "Hello");

        client.clear();

        assertEquals(0, client.getSentCount());
        assertEquals(0, client.getSentEmails().size());
    }
}
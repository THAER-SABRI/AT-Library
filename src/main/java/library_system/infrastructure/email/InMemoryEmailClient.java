package library_system.infrastructure.email;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple in-memory email client used primarily for testing purposes.
 * <p>
 * Instead of sending real emails, this client stores formatted email
 * messages in memory. Tests can then inspect sent messages, validate
 * recipients, and verify that the correct content was produced.
 * </p>
 */
public class InMemoryEmailClient {

    private final List<String> sentEmails = new ArrayList<>();

    /**
     * Simulates sending an email by storing it in memory.
     * <p>
     * The email is only recorded if:
     * <ul>
     *     <li>The recipient address is not null or blank</li>
     *     <li>The message is not null</li>
     * </ul>
     * Otherwise, the send attempt is ignored.
     * </p>
     *
     * @param to      the recipient address
     * @param message the email message content
     */
    public void send(String to, String message) {
        if (to == null || to.trim().isEmpty() || message == null) return;
        sentEmails.add("To: " + to + " | Message: " + message);
    }

    /**
     * Returns a copy of all emails that were "sent" during the lifetime
     * of this object.
     *
     * @return a list of formatted email entries; never {@code null}
     */
    public List<String> getSentEmails() {
        return new ArrayList<>(sentEmails);
    }

    /**
     * Returns the number of emails that have been recorded.
     *
     * @return the total count of sent emails
     */
    public int getSentCount() {
        return sentEmails.size();
    }

    /**
     * Clears all recorded sent emails.
     * <p>
     * Useful for resetting test state between test executions.
     * </p>
     */
    public void clear() {
        sentEmails.clear();
    }
}

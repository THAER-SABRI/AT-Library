package library_system.application;

/**
 * Service interface for sending email messages within the library system.
 * <p>
 * Implementations of this interface define how emails are delivered,
 * whether through SMTP, mock testing clients, or in-memory simulation.
 * </p>
 */
public interface EmailService {

    /**
     * Sends an email message to the specified recipient.
     *
     * @param to      the recipient's email address; must not be {@code null} or empty
     * @param subject the subject line of the email; must not be {@code null}
     * @param body    the main text content of the message; must not be {@code null}
     */
    void sendEmail(String to, String subject, String body);
}

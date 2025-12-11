package library_system.infrastructure.email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import library_system.application.EmailService;

import java.util.Properties;

/**
 * Email service implementation that sends real emails using SMTP.
 * <p>
 * This class relies on the Jakarta Mail API to authenticate with an SMTP
 * server and deliver email messages. Configuration values such as server host,
 * port, username, password, and TLS usage are provided through the constructor.
 * </p>
 *
 * <p>This implementation is intended for production use. For testing purposes,
 * an alternative like {@link InMemoryEmailClient} should be preferred.</p>
 */
public class SMTPEmailService implements EmailService {

    private final String username;
    private final String password;
    private final String host;
    private final int port;
    private final boolean useTls;

    /**
     * Creates a new SMTP-based email service.
     *
     * @param username the username (email address) used for authentication
     * @param password the application-specific or SMTP password
     * @param host     the SMTP server host (e.g., {@code smtp.gmail.com})
     * @param port     the SMTP server port (commonly 587 for TLS)
     * @param useTls   whether to enable STARTTLS encryption
     */
    public SMTPEmailService(String username, String password, String host, int port, boolean useTls) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.useTls = useTls;
    }

    /**
     * Sends an email using SMTP credentials configured for this service.
     * <p>
     * Steps performed:
     * <ol>
     *     <li>Builds SMTP properties</li>
     *     <li>Creates an authenticated mail session</li>
     *     <li>Constructs a {@link MimeMessage} with subject and content</li>
     *     <li>Sends the email via {@link Transport#send(Message)}</li>
     * </ol>
     *
     * @param to      the recipient's email address; must be valid
     * @param subject the email subject line; must not be {@code null}
     * @param body    the text content of the email; must not be {@code null}
     *
     * @throws RuntimeException if the message cannot be sent
     */
    @Override
    public void sendEmail(String to, String subject, String body) {
        Properties props = new Properties();

        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", String.valueOf(port));

        if (useTls) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        Session session = Session.getInstance(
                props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                }
        );

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}

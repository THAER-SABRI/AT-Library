package library_system.infrastructure.email;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import library_system.application.EmailService;

import java.util.Properties;

public class SMTPEmailService implements EmailService {

    private final String username;
    private final String password;
    private final String host;
    private final int port;
    private final boolean useTls;

    public SMTPEmailService(String username, String password, String host, int port, boolean useTls) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.port = port;
        this.useTls = useTls;
    }

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

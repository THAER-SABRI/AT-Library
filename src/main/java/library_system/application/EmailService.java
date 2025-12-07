package library_system.application;

public interface EmailService {
    void sendEmail(String to, String subject, String body);
}

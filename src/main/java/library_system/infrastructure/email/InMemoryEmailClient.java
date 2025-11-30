package library_system.infrastructure.email;

import java.util.ArrayList;
import java.util.List;

public class InMemoryEmailClient {
    private final List<String> sentEmails = new ArrayList<String>();

    public void send(String to, String message) {
        if (to == null || to.trim().isEmpty() || message == null) return;
        sentEmails.add("To: " + to + " | Message: " + message);
    }

    public List<String> getSentEmails() {
        return new ArrayList<String>(sentEmails);
    }

    public int getSentCount() {
        return sentEmails.size();
    }

    public void clear() {
        sentEmails.clear();
    }
}

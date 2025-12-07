package library_system.application;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class SendReminders {

    private static final String OVERDUE_FILE = "DATA/OVERDUE.TXT";
    private static final String USERS_FILE = "DATA/USERS.TXT";
    private static final String REMINDERS_FILE = "DATA/REMINDERS.TXT";

    private final EmailService emailService;
    private final ComputeFine computeFine;
    private final List<String> sentLogs = new ArrayList<>();

    public SendReminders(EmailService emailService, ComputeFine computeFine) {
        this.emailService = emailService;
        this.computeFine = computeFine;
    }

    public int sendAll(LocalDate today) {
        if (today == null) return 0;

        sentLogs.clear();

        Map<String, Integer> overdueByUser = loadOverdue(today);
        Map<String, String> userEmails = loadUserEmails();

        int sent = 0;

        for (Map.Entry<String, Integer> entry : overdueByUser.entrySet()) {
            String userId = entry.getKey();
            int overdueCount = entry.getValue();

            double outstanding = computeFine.computeOutstanding(userId, today);
            if (outstanding <= 0) continue;

            String email = userEmails.getOrDefault(userId, "").trim();
            boolean hasRealEmail = email.length() > 3 && email.contains("@");

            String message =
                    "📚 *Library Overdue Notice*\n\n" +
                    "Hello,\n\n" +
                    "You currently have *" + overdueCount + " overdue book(s)*.\n" +
                    "Your outstanding fine is: **" + outstanding + " NIS**.\n\n" +
                    "Kindly settle your fine and return the books at your earliest convenience.\n\n" +
                    "If you already resolved the issue, please ignore this message.\n\n" +
                    "Best regards,\n" +
                    "AT Library System";

            String recipient = hasRealEmail ? email : ("User:" + userId);

            if (hasRealEmail) {
                emailService.sendEmail(recipient, "Library Overdue Notice", message);
            }

            String logLine = "To:" + recipient + " | Books:" + overdueCount + " | Fine:" + outstanding;
            sentLogs.add(logLine);
            appendLog(logLine);

            sent++;
        }

        return sent;
    }

    private Map<String,Integer> loadOverdue(LocalDate today) {
        Map<String,Integer> map = new HashMap<>();
        Path p = Paths.get(OVERDUE_FILE);

        if (!Files.exists(p)) return map;

        try {
            for (String line : Files.readAllLines(p)) {
                if (!line.startsWith("|") || line.startsWith("| TYPE")) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 5) continue;

                String user = parts[3].trim();
                String dueStr = parts[4].trim();

                try {
                    LocalDate due = LocalDate.parse(dueStr);
                    if (!due.isAfter(today)) {
                        map.put(user, map.getOrDefault(user,0) + 1);
                    }
                } catch (Exception ignored) {}
            }
        } catch (IOException ignored) {}

        return map;
    }

    private Map<String,String> loadUserEmails() {
        Map<String,String> map = new HashMap<>();
        Path p = Paths.get(USERS_FILE);

        if (!Files.exists(p)) return map;

        try {
            for (String line : Files.readAllLines(p)) {
                if (!line.startsWith("|") || line.startsWith("| ID")) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 5) continue;

                map.put(parts[1].trim(), parts[4].trim());
            }
        } catch (IOException ignored) {}

        return map;
    }

    private void appendLog(String text) {
        try {
            ensureParent();
            try (BufferedWriter w = Files.newBufferedWriter(Paths.get(REMINDERS_FILE),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {
                w.write(text);
                w.newLine();
            }
        } catch (IOException ignored) {}
    }

    private void ensureParent() {
        try {
            Path parent = Paths.get(REMINDERS_FILE).getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (IOException ignored) {}
    }

    public List<String> getSentLogs() {
        return new ArrayList<>(sentLogs);
    }
}

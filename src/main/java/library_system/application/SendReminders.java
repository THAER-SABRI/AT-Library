package library_system.application;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

public class SendReminders {
    private static final String OVERDUE_FILE = "DATA/OVERDUE.TXT";
    private static final String USERS_FILE = "DATA/USERS.TXT";
    private static final String REMINDERS_LOG = "DATA/REMINDERS.TXT";

    private final List<String> sentLogs = new ArrayList<>();
    private final EmailService emailService;
    private final ComputeFine computeFine;
    
    
    public SendReminders(EmailService emailService, ComputeFine computeFine) {
        this.emailService = emailService;
        this.computeFine = computeFine;
    }
   
    public int sendAll(LocalDate today) {
        if (today == null) return 0;
        Map<String, Integer> overdueCounts = getOverdueCountByUser(today);
        Map<String, String> userEmails = loadUserEmails();
        int sent = 0;

        for (Map.Entry<String, Integer> entry : overdueCounts.entrySet()) {
            String userId = entry.getKey();
            int count = entry.getValue();
            String email = userEmails.getOrDefault(userId, "");
            String recipient = (email == null || email.trim().isEmpty() || "-".equals(email.trim()))
                    ? ("User:" + userId)
                    : email;

            double fine = computeFine.computeOutstanding(userId, today);

            String msg =
                    "📚 Library Overdue Notice\n\n" +
                    "Dear user,\n\n" +
                    "You currently have *" + count + " overdue book(s)*.\n" +
                    "Your outstanding fine at this moment is: **" + fine + " NIS**.\n\n" +
                    "Please return your books and settle your fine as soon as possible to avoid further penalties.\n\n" +
                    "If you already resolved this issue, kindly ignore this email.\n\n" +
                    "Best regards,\n" +
                    "AT Library System";

            
            if (!recipient.startsWith("User:")) {
                emailService.sendEmail(recipient, "Library Overdue Notice", msg);
            }
            
            String log = "To: " + recipient + " | Message: " + msg;
            sentLogs.add(log);
            appendReminderLog(log);
            sent++;
        }

        return sent;
    }

    private Map<String, Integer> getOverdueCountByUser(LocalDate today) {
        Map<String, Integer> counts = new HashMap<>();
        Path path = Paths.get(OVERDUE_FILE);
        if (!Files.exists(path)) return counts;

        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) continue;
                if (line.startsWith("| TYPE") || !line.startsWith("|")) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 5) continue;

                String type = parts[1].trim();
                String isbn = parts[2].trim();
                String userId = parts[3].trim();
                String dueDateStr = parts[4].trim();

                try {
                    LocalDate dueDate = LocalDate.parse(dueDateStr);
                    if (!dueDate.isAfter(today)) {
                        counts.put(userId, counts.getOrDefault(userId, 0) + 1);
                    }
                } catch (Exception ignored) {}
            }
        } catch (IOException e) {
            System.out.println("Error reading overdue file: " + e.getMessage());
        }

        return counts;
    }

    private Map<String, String> loadUserEmails() {
        Map<String, String> emails = new HashMap<>();
        Path path = Paths.get(USERS_FILE);
        if (!Files.exists(path)) return emails;

        try {
            List<String> lines = Files.readAllLines(path);
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) continue;
                if (line.startsWith("| ID") || !line.startsWith("|")) continue;
                String[] parts = line.split("\\|");
                if (parts.length < 5) continue;

                String id = parts[1].trim();
                String email = parts[4].trim();
                if (!id.isEmpty()) emails.put(id, email);
            }
        } catch (IOException e) {
            System.out.println("Error reading users file: " + e.getMessage());
        }

        return emails;
    }

    private void appendReminderLog(String line) {
        try {
            ensureParentDir(REMINDERS_LOG);
            try (BufferedWriter w = Files.newBufferedWriter(Paths.get(REMINDERS_LOG),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                w.write(line);
                w.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing reminders log: " + e.getMessage());
        }
    }

    private void ensureParentDir(String file) {
        try {
            Path p = Paths.get(file);
            Path parent = p.getParent();
            if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
        } catch (IOException ignored) {}
    }

    public List<String> getSentLogs() {
        return new ArrayList<>(sentLogs);
    }
}

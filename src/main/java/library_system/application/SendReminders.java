package library_system.application;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

/**
 * Service responsible for sending overdue reminders to users who have
 * outstanding fines and overdue media items.
 * <p>
 * This class reads from:
 * <ul>
 *     <li>{@code OVERDUE_FILE} – to determine which users have overdue books</li>
 *     <li>{@code USERS_FILE} – to retrieve user email addresses</li>
 *     <li>{@code REMINDERS_FILE} – to append reminder logs</li>
 * </ul>
 * <p>
 * A reminder email is sent only if:
 * <ul>
 *     <li>The user has overdue items</li>
 *     <li>The user has unpaid fines</li>
 *     <li>A valid email address is available</li>
 * </ul>
 * Regardless of email availability, a reminder entry is always logged.
 * </p>
 */
public class SendReminders {

    private static final String OVERDUE_FILE = "DATA/OVERDUE.TXT";
    private static final String USERS_FILE = "DATA/USERS.TXT";
    private static final String REMINDERS_FILE = "DATA/REMINDERS.TXT";

    private final EmailService emailService;
    private final ComputeFine computeFine;
    private final List<String> sentLogs = new ArrayList<>();

    /**
     * Creates a new {@code SendReminders} instance.
     *
     * @param emailService the service used to send emails; must not be {@code null}
     * @param computeFine  the service used to compute outstanding fines; must not be {@code null}
     */
    public SendReminders(EmailService emailService, ComputeFine computeFine) {
        this.emailService = emailService;
        this.computeFine = computeFine;
    }

    /**
     * Sends reminder messages to all users who have overdue items and unpaid fines.
     * <p>
     * The process:
     * <ol>
     *     <li>Clears previous logs</li>
     *     <li>Loads overdue statuses from file</li>
     *     <li>Loads user emails from file</li>
     *     <li>For each user with overdue items, checks outstanding fines</li>
     *     <li>If the fine is positive, sends an email (if email exists)</li>
     *     <li>Logs every reminder sent or attempted</li>
     * </ol>
     *
     * @param today the reference date for determining overdue status; must not be {@code null}
     * @return the number of reminders processed (emails sent or logged)
     */
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

    /**
     * Loads overdue entries from {@code OVERDUE_FILE}.
     * <p>
     * The method parses each line, extracting:
     * <ul>
     *     <li>User ID</li>
     *     <li>Due date</li>
     * </ul>
     * If the due date is not after the given {@code today} date,
     * the user is counted as having an overdue.
     *
     * @param today the date to compare against due dates
     * @return a map of user IDs to the number of overdue items they have
     */
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
                        map.put(user, map.getOrDefault(user, 0) + 1);
                    }
                } catch (Exception ignored) {}
            }
        } catch (IOException ignored) {}

        return map;
    }

    /**
     * Loads user email addresses from {@code USERS_FILE}.
     * <p>
     * Each valid line is expected to be in a table-like format, with:
     * <ul>
     *     <li>User ID</li>
     *     <li>Email address</li>
     * </ul>
     *
     * @return a mapping of user IDs to their associated email addresses
     */
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

    /**
     * Appends a single reminder log entry to {@code REMINDERS_FILE}.
     *
     * @param text the log message to append
     */
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

    /**
     * Ensures the parent directory for {@code REMINDERS_FILE} exists.
     * If not, it is created.
     */
    private void ensureParent() {
        try {
            Path parent = Paths.get(REMINDERS_FILE).getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (IOException ignored) {}
    }

    /**
     * Returns a copy of the reminder log entries generated during the
     * most recent {@link #sendAll(LocalDate)} execution.
     *
     * @return a list of log messages; never {@code null}
     */
    public List<String> getSentLogs() {
        return new ArrayList<>(sentLogs);
    }
}

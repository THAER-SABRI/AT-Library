package library_system.infrastructure.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import library_system.application.PaymentLedger;

/**
 * File-based implementation of the {@link PaymentLedger} interface.
 * <p>
 * Payments are stored in a text file using a fixed-width, pipe-separated format:
 *
 * <pre>
 * | USER_ID     | AMOUNT     | DATE        |
 * | user1       | 10.00      | 2025-01-17  |
 * | user1       | 5.50       | 2025-01-20  |
 * </pre>
 *
 * Each call to {@link #recordPayment(String, double, LocalDate)} appends a new row.
 * <p>
 * The ledger supports:
 * <ul>
 *   <li>Recording payments</li>
 *   <li>Computing total paid for a user</li>
 *   <li>Loading raw ledger lines</li>
 *   <li>Resetting the file while preserving the header</li>
 * </ul>
 */
public class FilePaymentLedger implements PaymentLedger {

    private final String file;

    // Column widths for formatted output
    private static final int W_USER = 12;
    private static final int W_AMT  = 10;
    private static final int W_DATE = 12;

    /**
     * Creates a payment ledger using the default file {@code DATA/PAYMENTS.TXT}.
     */
    public FilePaymentLedger() {
        this("DATA/PAYMENTS.TXT");
    }

    /**
     * Creates a payment ledger with a custom file location.
     *
     * @param file path to the payments ledger file
     */
    public FilePaymentLedger(String file) {
        this.file = file;
    }

    /**
     * Records a payment in the ledger.
     * <p>
     * The method:
     * <ul>
     *     <li>Validates input</li>
     *     <li>Ensures the directory and header exist</li>
     *     <li>Appends a padded ledger row</li>
     * </ul>
     *
     * @param userId the paying user's ID; must not be blank
     * @param amount the payment amount
     * @param date   the payment date
     */
    @Override
    public void recordPayment(String userId, double amount, LocalDate date) {
        if (userId == null || userId.trim().isEmpty() || date == null) return;

        ensureParentDir();
        ensureHeaderExists();
        append(row(userId.trim(), String.format("%.2f", amount), date.toString()));
    }

    /**
     * Computes the total amount paid by a specific user.
     * <p>
     * Parsing logic:
     * <ul>
     *     <li>Removes pipe characters</li>
     *     <li>Splits on whitespace</li>
     *     <li>Matches the user in column 1</li>
     *     <li>Sums the AMOUNT column</li>
     * </ul>
     *
     * @param userId the user ID
     * @return total paid, or {@code 0.0} if none or invalid input
     */
    @Override
    public double totalPaidForUser(String userId) {
        if (userId == null || userId.trim().isEmpty()) return 0.0;
        double sum = 0.0;

        for (String line : findAll()) {
            String cleaned = line.replace("|", "").trim();
            String[] parts = cleaned.split("\\s+");
            if (parts.length < 3) continue;
            if (!parts[0].equals(userId)) continue;

            try {
                sum += Double.parseDouble(parts[1]);
            } catch (Exception ignored) {}
        }
        return sum;
    }

    /**
     * Reads all payment ledger lines.
     *
     * @return a list of strings, one per ledger row
     */
    @Override
    public List<String> findAll() {
        ensureParentDir();
        Path p = Paths.get(file);
        if (!Files.exists(p)) return new ArrayList<>();
        try {
            return Files.readAllLines(p);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Deletes all ledger contents and recreates an empty file with a header.
     */
    public void deleteAll() {
        ensureParentDir();
        try {
            Files.deleteIfExists(Paths.get(file));
            ensureHeaderExists();
        } catch (IOException ignored) {}
    }

    /**
     * Creates the header row for the ledger file.
     *
     * @return formatted header string
     */
    private String header() {
        return row("USER_ID", "AMOUNT", "DATE");
    }

    /**
     * Ensures that the persistence file exists and has a header row.
     */
    private void ensureHeaderExists() {
        try {
            Path p = Paths.get(file);
            if (!Files.exists(p) || Files.size(p) == 0) {
                Files.write(p,
                        Collections.singletonList(header()),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException ignored) {}
    }

    /**
     * Builds a properly padded row of the form:
     * <pre>
     * | user123     | 10.00      | 2025-01-17  |
     * </pre>
     *
     * @param user   user ID
     * @param amount payment amount
     * @param date   payment date
     * @return formatted row
     */
    private String row(String user, String amount, String date) {
        return "| "
                + pad(user, W_USER) + " | "
                + pad(amount, W_AMT) + " | "
                + pad(date, W_DATE) + " |";
    }

    /**
     * Pads a string to a fixed width using spaces.
     *
     * @param s the string
     * @param w desired width
     * @return padded string
     */
    private String pad(String s, int w) {
        if (s == null) s = "";
        if (s.length() > w) return s.substring(0, w);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < w) sb.append(' ');
        return sb.toString();
    }

    /**
     * Appends a line to the payment ledger file.
     *
     * @param line the text to append
     */
    private void append(String line) {
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get(file),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            w.write(line);
            w.newLine();
        } catch (IOException ignored) {}
    }

    /**
     * Ensures that the parent directory of the file exists.
     */
    private void ensureParentDir() {
        try {
            Path p = Paths.get(file);
            Path parent = p.getParent();
            if (parent != null && !Files.exists(parent))
                Files.createDirectories(parent);
        } catch (IOException ignored) {}
    }
}

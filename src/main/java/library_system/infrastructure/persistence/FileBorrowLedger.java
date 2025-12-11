package library_system.infrastructure.persistence;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import library_system.application.BorrowLedger;

/**
 * File-based implementation of the {@link BorrowLedger} interface.
 * <p>
 * This ledger records book and CD borrow/return actions in a formatted
 * pipe-separated text file. Each operation is represented as a row with
 * padded columns for alignment:
 *
 * <pre>
 * | TYPE    | ISBN         | USER_ID     | DUE_DATE    |
 * | BORROW  | 123456789012 | user42      | 2025-01-01  |
 * | RETURN  | 123456789012 | user42      |             |
 * </pre>
 *
 * The ledger maintains:
 * <ul>
 *     <li>A persistent log of borrowing transactions</li>
 *     <li>Removal of old BORROW entries when a RETURN is logged</li>
 *     <li>Automatic creation of the file and its header</li>
 *     <li>Formatted alignment for readability</li>
 * </ul>
 */
public class FileBorrowLedger implements BorrowLedger {

    private final String filePath;

    // Column widths for aligned output
    private static final int W_TYPE = 8;
    private static final int W_ISBN = 12;
    private static final int W_USER = 12;
    private static final int W_DUE  = 12;

    /**
     * Creates a ledger using the default file path: {@code DATA/BORROWS.TXT}.
     */
    public FileBorrowLedger() {
        this("DATA/BORROWS.TXT");
    }

    /**
     * Creates a ledger that writes to a custom file path.
     *
     * @param filePath the location where ledger entries will be stored
     */
    public FileBorrowLedger(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Records a borrow transaction in the ledger.
     * <p>
     * This method:
     * <ul>
     *     <li>Ensures the header exists</li>
     *     <li>Appends a BORROW row with ISBN, user ID, and due date</li>
     * </ul>
     *
     * @param isbn     the ISBN of the borrowed item
     * @param userId   the ID of the user borrowing the item
     * @param dueDate  the assigned due date
     */
    @Override
    public void recordBorrow(String isbn, String userId, LocalDate dueDate) {
        if (isbn == null || userId == null || dueDate == null) return;
        ensureHeader();
        append(row("BORROW", isbn, userId, dueDate.toString()));
    }

    /**
     * Records a return transaction in the ledger.
     * <p>
     * The method behaves as follows:
     * <ul>
     *     <li>Ensures the header exists</li>
     *     <li>Removes any matching BORROW rows for the same ISBN and user</li>
     *     <li>Appends a RETURN row without a due date</li>
     * </ul>
     *
     * @param isbn   the ISBN of the returned item
     * @param userId the ID of the user returning the item
     */
    @Override
    public void recordReturn(String isbn, String userId) {
        if (isbn == null || userId == null) return;
        ensureHeader();
        removeOldBorrow(isbn, userId);
        append(row("RETURN", isbn, userId, ""));
    }

    /**
     * Removes old BORROW entries for the specified ISBN and user.
     * <p>
     * This ensures that the ledger does not contain stale borrow entries
     * once a RETURN is logged.
     * </p>
     *
     * @param isbn   the borrowed item's ISBN
     * @param userId the user who borrowed it
     */
    private void removeOldBorrow(String isbn, String userId) {
        try {
            List<String> all = findAll();
            List<String> filtered = new ArrayList<>();

            for (String line : all) {
                boolean match = line.contains("| BORROW")
                        && line.contains("| " + isbn)
                        && line.contains("| " + userId);
                if (!match) filtered.add(line);
            }

            Files.write(Paths.get(filePath), filtered,
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE);

        } catch (Exception ignored) {}
    }

    /**
     * Reads all lines from the ledger file.
     * <p>
     * If the file does not exist, an empty list is returned.
     * </p>
     *
     * @return a list of all ledger lines
     */
    @Override
    public List<String> findAll() {
        ensureParentDir();
        List<String> lines = new ArrayList<>();
        if (!Files.exists(Paths.get(filePath))) return lines;
        try {
            lines = Files.readAllLines(Paths.get(filePath));
        } catch (IOException ignored) {}
        return lines;
    }

    /**
     * Reloads ledger entries into an externally provided list.
     *
     * @param targetList the list to populate with ledger contents
     */
    public void reloadFromFile(List<String> targetList) {
        targetList.clear();
        targetList.addAll(findAll());
    }

    /**
     * Deletes all ledger entries and recreates the file header.
     */
    public void deleteAll() {
        try {
            ensureParentDir();
            Files.deleteIfExists(Paths.get(filePath));
            ensureHeader();
        } catch (IOException ignored) {}
    }

    /**
     * Builds the header row for the ledger table.
     *
     * @return a formatted header line
     */
    private String header() {
        return row("TYPE", "ISBN", "USER_ID", "DUE_DATE");
    }

    /**
     * Ensures the file exists and contains the header row.
     */
    private void ensureHeader() {
        try {
            ensureParentDir();
            Path path = Paths.get(filePath);
            if (!Files.exists(path) || Files.size(path) == 0) {
                Files.write(path, Arrays.asList(header()),
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException ignored) {}
    }

    /**
     * Formats a ledger row with fixed-width padded fields.
     *
     * @param type transaction type (BORROW or RETURN)
     * @param isbn item ISBN
     * @param user user ID
     * @param due  due date or empty string
     * @return a formatted pipe-separated line
     */
    private String row(String type, String isbn, String user, String due) {
        return new StringBuilder()
                .append("| ").append(pad(type, W_TYPE)).append(" | ")
                .append(pad(isbn, W_ISBN)).append(" | ")
                .append(pad(user, W_USER)).append(" | ")
                .append(pad(due, W_DUE)).append(" |")
                .toString();
    }

    /**
     * Pads a string with spaces to match a target width.
     *
     * @param s the string to pad
     * @param w the width of the column
     * @return the padded string
     */
    private String pad(String s, int w) {
        if (s == null) s = "";
        if (s.length() > w) return s.substring(0, w);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < w) sb.append(' ');
        return sb.toString();
    }

    /**
     * Appends a row to the ledger file.
     *
     * @param line the line to append
     */
    private void append(String line) {
        try {
            ensureParentDir();
            try (BufferedWriter w = Files.newBufferedWriter(Paths.get(filePath),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND)) {
                w.write(line);
                w.newLine();
            }
        } catch (IOException ignored) {}
    }

    /**
     * Ensures that the parent directory of the ledger file exists.
     * <p>
     * If missing, it is created automatically.
     * </p>
     */
    private void ensureParentDir() {
        try {
            Path p = Paths.get(filePath);
            Path parent = p.getParent();
            if (parent != null && !Files.exists(parent))
                Files.createDirectories(parent);
        } catch (IOException ignored) {}
    }
}

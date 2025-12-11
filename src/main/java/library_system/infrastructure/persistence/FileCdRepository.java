package library_system.infrastructure.persistence;

import library_system.application.CdRepository;
import library_system.domain.CD;
import library_system.domain.strategy.BorrowDurationStrategy;
import library_system.domain.strategy.FineStrategy;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * File-based implementation of the {@link CdRepository} interface.
 * <p>
 * This repository loads and saves CD records from a pipe-separated text file
 * using the following format:
 *
 * <pre>
 * | ID | TITLE | STATUS | USER_ID | DUE_DATE |
 * </pre>
 *
 * Example row:
 * <pre>
 * | CD001 | Jazz Collection | BORROWED | user42 | 2025-01-15 |
 * </pre>
 *
 * STATUS may be:
 * <ul>
 *   <li>{@code FREE}</li>
 *   <li>{@code BORROWED}</li>
 * </ul>
 *
 * USER_ID and DUE_DATE use {@code null} to indicate absence.
 * <p>
 * Borrow and fine strategies are supplied externally so that the
 * repository can construct complete domain objects.
 * </p>
 */
public class FileCdRepository implements CdRepository {

    private final String file;
    private final BorrowDurationStrategy borrowStrategy;
    private final FineStrategy fineStrategy;

    /**
     * Creates a CD repository with a specific file location.
     *
     * @param file           the path to the persistence file
     * @param borrowStrategy strategy defining CD borrowing duration
     * @param fineStrategy   strategy defining CD overdue fines
     */
    public FileCdRepository(String file,
                            BorrowDurationStrategy borrowStrategy,
                            FineStrategy fineStrategy) {
        this.file = file;
        this.borrowStrategy = borrowStrategy;
        this.fineStrategy = fineStrategy;
    }

    /**
     * Creates a CD repository using the default file {@code DATA/CDS.TXT}.
     *
     * @param borrowStrategy strategy defining CD borrowing duration
     * @param fineStrategy   strategy defining CD overdue fines
     */
    public FileCdRepository(BorrowDurationStrategy borrowStrategy,
                            FineStrategy fineStrategy) {
        this("DATA/CDS.TXT", borrowStrategy, fineStrategy);
    }

    /**
     * Loads all CDs from the repository file.
     * <p>
     * The method:
     * <ul>
     *     <li>Ensures the directory exists</li>
     *     <li>Reads each non-empty line</li>
     *     <li>Strips leading/trailing pipe characters</li>
     *     <li>Parses ID, title, borrow status, user ID, and due date</li>
     *     <li>Constructs a {@link CD} object with provided strategies</li>
     *     <li>Reconstructs borrow state when applicable</li>
     * </ul>
     *
     * @return a list of parsed CD objects; never {@code null}
     */
    @Override
    public List<CD> getAll() {
        ensureParentDir();
        List<CD> list = new ArrayList<>();
        Path path = Paths.get(file);
        if (!Files.exists(path)) return list;

        try {
            for (String raw : Files.readAllLines(path)) {


                if (raw == null) {
                    continue;
                }
                String line = raw.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.startsWith("|")) line = line.substring(1);
                if (line.endsWith("|")) line = line.substring(0, line.length() - 1);

                String[] parts = line.split("\\|");
                if (parts.length < 5) continue;

                for (int i = 0; i < parts.length; i++) parts[i] = parts[i].trim();

                // Skip header row
                if ("ID".equalsIgnoreCase(parts[0])) continue;

                if (parts.length < 5) {
                    continue;
                }
                for (int i = 0; i < parts.length; i++) {
                    parts[i] = parts[i].trim();
                }
                if ("ID".equalsIgnoreCase(parts[0])) {
                    continue;
                }
                String id = parts[0];
                String title = parts[1];
                String status = parts[2];
                String user = parts[3];
                String due = parts[4];

                CD cd = new CD(id, title, borrowStrategy, fineStrategy);

                if ("BORROWED".equalsIgnoreCase(status)) {
                    LocalDate d =
                            ("null".equalsIgnoreCase(due) || due.isBlank())
                                    ? null
                                    : LocalDate.parse(due);

                    cd.borrow(user.equals("null") ? null : user, d);
                }

                list.add(cd);
            }
        } catch (IOException ignored) {}

        return list;
    }

    /**
     * Saves all CD entries to the persistence file.
     * <p>
     * Existing content is overwritten. The method writes a header row followed
     * by one formatted entry per CD.
     * </p>
     *
     * @param cds the list of CDs to persist; must not be {@code null}
     */
    @Override
    public void saveAll(List<CD> cds) {
        ensureParentDir();
        Path path = Paths.get(file);

        try (BufferedWriter w = Files.newBufferedWriter(
                path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            w.write(row("ID", "TITLE", "STATUS", "USER_ID", "DUE_DATE"));
            w.newLine();

            for (CD cd : cds) {
                String status = cd.isBorrowed() ? "BORROWED" : "FREE";
                String user = cd.getBorrowerId() == null ? "null" : cd.getBorrowerId();
                String due = cd.getDueDate() == null ? "null" : cd.getDueDate().toString();

                w.write(row(cd.getId(), cd.getTitle(), status, user, due));
                w.newLine();
            }
        } catch (IOException ignored) {}
    }

    /**
     * Formats a row into the pipe-separated storage layout.
     *
     * @param cols column values
     * @return a formatted row such as <code>| A | B | C |</code>
     */
    private String row(String... cols) {
        return "| " + String.join(" | ", cols) + " |";
    }

    /**
     * Ensures the directory for the file exists.
     * <p>
     * If the parent directory is missing, it is automatically created.
     * </p>
     */
    private void ensureParentDir() {
        try {
            Path p = Paths.get(file);
            Path parent = p.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (IOException ignored) {}
    }
}

package library_system.infrastructure.persistence;

import library_system.application.SettingsGateway;

import java.io.IOException;
import java.nio.file.*;

/**
 * File-based implementation of the {@link SettingsGateway} interface.
 * <p>
 * This class stores and retrieves simple configuration settings (loan durations)
 * using a very small key-value file format:
 *
 * <pre>
 * loanDays=28
 * </pre>
 *
 * Only one setting is stored in the file: the loan duration. Both book and CD
 * durations map to the same value for simplicity.
 * <p>
 * If the file is missing, unreadable, or contains invalid data, it is recreated
 * with a default value of 28 days.
 * </p>
 */
public class FileSettingsGateway implements SettingsGateway {

    private final String file;

    /**
     * Creates a settings gateway that reads/writes from a specified file.
     *
     * @param file the path to the settings file
     */
    public FileSettingsGateway(String file) {
        this.file = file;
    }

    /**
     * Retrieves the loan duration for books.
     * <p>
     * Behavior:
     * <ul>
     *     <li>Ensures the file exists</li>
     *     <li>Attempts to parse a <code>loanDays=VALUE</code> pair</li>
     *     <li>If parsing fails, resets the value to the default (28)</li>
     * </ul>
     *
     * @return the configured loan duration in days; always positive
     */
    @Override
    public int getLoanDays() {
        ensureFileExists();

        try {
            String content = Files.readString(Paths.get(file));
            String[] parts = content.split("=");

            if (parts.length == 2) {
                int value = Integer.parseInt(parts[1].trim());
                if (value > 0) return value;
            }
        } catch (Exception ignored) {}

        // If invalid or missing, reset to default
        setLoanDays(28);
        return 28;
    }

    /**
     * Updates the loan duration for books.
     *
     * @param days the desired loan duration; if invalid, defaults to 28
     */
    @Override
    public void setLoanDays(int days) {
        if (days <= 0) {
            write("loanDays=28");
            return;
        }
        write("loanDays=" + days);
    }

    /**
     * Ensures that the settings file exists.
     * <p>
     * If missing, it is created with a default value of 28 days.
     * </p>
     */
    private void ensureFileExists() {
        Path p = Paths.get(file);
        if (!Files.exists(p)) {
            write("loanDays=28");
        }
    }

    /**
     * Writes text into the settings file, replacing its contents.
     *
     * @param s the file content to write
     */
    private void write(String s) {
        try {
            Files.writeString(Paths.get(file), s);
        } catch (IOException ignored) {}
    }

    /**
     * Retrieves the loan duration for CDs.
     * <p>
     * CD loan days behave identically to book loan days in this implementation.
     * </p>
     *
     * @return the configured CD loan duration in days
     */
    @Override
    public int getCdLoanDays() {
        return getLoanDays();
    }

    /**
     * Updates the loan duration for CDs.
     * <p>
     * CD and book durations are stored together, so this call maps directly to
     * {@link #setLoanDays(int)}.
     * </p>
     *
     * @param days the new loan duration for CDs
     */
    @Override
    public void setCdLoanDays(int days) {
        setLoanDays(days);
    }
}

package library_system.infrastructure.persistence;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import library_system.application.UserDirectory;

/**
 * File-based implementation of the {@link UserDirectory} interface.
 * <p>
 * This class stores user records in a fixed-width, pipe-separated text table.
 * The file format:
 *
 * <pre>
 * | ID           | NAME                   | PHONE           | EMAIL                       |
 * | u100         | Ali Zaid               | 0599999999      | ali@example.com             |
 * </pre>
 *
 * - The first line is always the header row.
 * - Each additional line represents a user.
 * - All columns are padded to fixed widths for readability.
 *
 * <p>
 * Methods in this class automatically:
 * <ul>
 *     <li>Create the parent directory if missing</li>
 *     <li>Ensure the header row exists</li>
 *     <li>Prevent duplicate user IDs</li>
 *     <li>Rewrite the entire file when deleting a user</li>
 * </ul>
 */
public class FileUserDirectory implements UserDirectory {

    private final String file;

    /**
     * Creates a user directory that stores data under {@code DATA/USERS.TXT}.
     */
    public FileUserDirectory() {
        this("DATA/USERS.TXT");
    }

    /**
     * Creates a user directory using the given file path.
     *
     * @param file path to the storage file
     */
    public FileUserDirectory(String file) {
        this.file = file;
    }

    /**
     * Retrieves all user entries including the header.
     * <p>
     * If the file does not exist, it is created with the header row.
     *
     * @return a list of representing lines from the file
     */
    @Override
    public List<String> getAllUsers() {
        ensureParentDir();
        List<String> users = new ArrayList<>();
        if (!Files.exists(Paths.get(file))) {
            ensureHeader();
            return users;
        }
        try {
            users = Files.readAllLines(Paths.get(file));
        } catch (IOException ignored) {}
        return users;
    }

    /**
     * Adds a user to the directory.
     * <p>
     * Behavior:
     * <ul>
     *     <li>Ensures the file and header exist</li>
     *     <li>Rejects null or blank IDs</li>
     *     <li>Rejects duplicates (if an entry starts with <code>| id</code>)</li>
     *     <li>Appends a formatted row for the new user</li>
     * </ul>
     *
     * @param id    user ID
     * @param name  full name
     * @param phone phone number
     * @param email email address
     */
    @Override
    public void addUser(String id, String name, String phone, String email) {
        if (id == null || id.trim().isEmpty()) return;
        ensureHeader();

        List<String> users = getAllUsers();
        for (String u : users) {
            if (u.startsWith("| " + id + " ")) return; // Duplicate ID
        }

        append(row(id, safe(name), safe(phone), safe(email)));
    }

    /**
     * Removes a user by ID.
     * <p>
     * Behavior:
     * <ul>
     *     <li>Ensures header exists</li>
     *     <li>Builds a new list without the matching user</li>
     *     <li>Rewrites the file if removal occurs</li>
     * </ul>
     *
     * @param id the user ID to remove
     * @return true if the user was found and removed
     */
    @Override
    public boolean removeUser(String id) {
        if (id == null || id.trim().isEmpty()) return false;
        ensureHeader();

        List<String> users = getAllUsers();
        List<String> updated = new ArrayList<>();
        boolean removed = false;

        for (String u : users) {
            if (u.startsWith("| " + id + " ")) {
                removed = true;
            } else {
                updated.add(u);
            }
        }

        if (!removed) return false;

        writeAll(updated);
        return true;
    }

    /**
     * Retrieves the email address associated with a user ID.
     * <p>
     * Each row is split on the pipe character; the email resides in column 4.
     *
     * @param userId the user ID to search for
     * @return the user's email, or {@code null} if not found
     */
    @Override
    public String getEmail(String userId) {
        List<String> lines = getAllUsers();
        for (String line : lines) {
            String[] p = line.split("\\|");
            if (p.length >= 5) {
                String id = p[1].trim();
                String email = p[4].trim();
                if (id.equals(userId)) return email;
            }
        }
        return null;
    }

    /**
     * Deletes the entire file and recreates it with the header.
     */
    public void deleteAll() {
        try {
            ensureParentDir();
            Files.deleteIfExists(Paths.get(file));
            ensureHeader();
        } catch (IOException ignored) {}
    }

    /**
     * Reloads all file contents into the given list.
     *
     * @param targetList list to overwrite with user entries
     */
    public void reloadFromFile(List<String> targetList) {
        targetList.clear();
        targetList.addAll(getAllUsers());
    }

    /**
     * Appends a new row to the file.
     *
     * @param line formatted user row
     */
    private void append(String line) {
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get(file),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(line);
            w.newLine();
        } catch (IOException ignored) {}
    }

    /**
     * Ensures the header row exists at the top of the file.
     */
    private void ensureHeader() {
        try {
            ensureParentDir();
            Path path = Paths.get(file);
            if (!Files.exists(path) || Files.size(path) == 0) {
                Files.write(path,
                        Arrays.asList(row("ID", "NAME", "PHONE", "EMAIL")),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException ignored) {}
    }

    /**
     * Formats a user record into a fixed-width pipe-separated row.
     *
     * @param id    user ID
     * @param name  name
     * @param phone phone number
     * @param email email address
     * @return formatted row string
     */
    private String row(String id, String name, String phone, String email) {
        return new StringBuilder()
                .append("| ").append(pad(id, 12)).append(" | ")
                .append(pad(name, 22)).append(" | ")
                .append(pad(phone, 16)).append(" | ")
                .append(pad(email, 28)).append(" |")
                .toString();
    }

    /**
     * Pads the given string to a fixed width with spaces.
     *
     * @param s string to pad
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
     * Ensures null values do not appear in user records.
     *
     * @param s any string
     * @return trimmed string or empty string if null
     */
    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    /**
     * Ensures the file's parent directory exists.
     * <p>
     * Creates the directory if missing.
     * </p>
     */
    private void ensureParentDir() {
        try {
            Path parent = Paths.get(file).getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (IOException ignored) {}
    }

    /**
     * Writes the full list of rows to the file, overwriting previous contents.
     *
     * @param lines rows to write to the file
     */
    private void writeAll(List<String> lines) {
        try {
            Files.write(Paths.get(file), lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ignored) {}
    }
}

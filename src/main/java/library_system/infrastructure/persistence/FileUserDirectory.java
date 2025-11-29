package library_system.infrastructure.persistence;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import library_system.application.UserDirectory;

public class FileUserDirectory implements UserDirectory {
    private static final String FILE = "DATA/USERS.TXT";
    private static final int W_ID = 12;
    private static final int W_NAME = 22;
    private static final int W_PHONE = 16;
    private static final int W_EMAIL = 28;

    @Override
    public List<String> getAllUsers() {
        ensureParentDir();
        List<String> users = new ArrayList<>();
        if (!Files.exists(Paths.get(FILE))) {
            ensureHeader();
            return users;
        }
        try {
            users = Files.readAllLines(Paths.get(FILE));
        } catch (IOException e) {
            System.out.println("Error reading users file: " + e.getMessage());
        }
        return users;
    }

    @Override
    public void addUser(String id, String name, String phone, String email) {
        if (id == null || id.trim().isEmpty()) return;
        ensureHeader();

        // Prevent duplicate user IDs
        List<String> users = getAllUsers();
        for (String u : users) {
            if (u.startsWith("| " + id + " ")) {
                System.out.println("User with ID " + id + " already exists.");
                return;
            }
        }

        String line = row(id, safe(name), safe(phone), safe(email));
        append(line);
    }

    public void deleteAll() {
        try {
            ensureParentDir();
            Files.deleteIfExists(Paths.get(FILE));
            ensureHeader();
        } catch (IOException e) {
            System.out.println("Error deleting users file: " + e.getMessage());
        }
    }

    public void reloadFromFile(List<String> targetList) {
        targetList.clear();
        targetList.addAll(getAllUsers());
    }

    private void append(String line) {
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(FILE),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to users file: " + e.getMessage());
        }
    }

    private void ensureHeader() {
        try {
            ensureParentDir();
            Path path = Paths.get(FILE);
            if (!Files.exists(path) || Files.size(path) == 0) {
                Files.write(path, Arrays.asList(row("ID", "NAME", "PHONE", "EMAIL")),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException e) {
            System.out.println("Error creating users header: " + e.getMessage());
        }
    }

    private String row(String id, String name, String phone, String email) {
        return new StringBuilder()
                .append("| ").append(pad(id, W_ID)).append(" | ")
                .append(pad(name, W_NAME)).append(" | ")
                .append(pad(phone, W_PHONE)).append(" | ")
                .append(pad(email, W_EMAIL)).append(" |")
                .toString();
    }

    private String pad(String s, int w) {
        if (s == null) s = "";
        if (s.length() > w) return s.substring(0, w);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < w) sb.append(' ');
        return sb.toString();
    }

    private static String safe(String s) {
        return s == null ? "" : s.trim();
    }

    private void ensureParentDir() {
        try {
            Path parent = Paths.get(FILE).getParent();
            if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
        } catch (IOException e) {
            System.out.println("Error ensuring directory: " + e.getMessage());
        }
    }
}

package library_system.infrastructure.persistence;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import library_system.application.UserDirectory;

public class FileUserDirectory implements UserDirectory {

    private final String file;

    public FileUserDirectory() {
        this("DATA/USERS.TXT");
    }

    public FileUserDirectory(String file) {
        this.file = file;
    }

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

    @Override
    public void addUser(String id, String name, String phone, String email) {
        if (id == null || id.trim().isEmpty()) return;
        ensureHeader();
        List<String> users = getAllUsers();
        for (String u : users) {
            if (u.startsWith("| " + id + " ")) return;
        }
        append(row(id, safe(name), safe(phone), safe(email)));
    }

    public void deleteAll() {
        try {
            ensureParentDir();
            Files.deleteIfExists(Paths.get(file));
            ensureHeader();
        } catch (IOException ignored) {}
    }

    public void reloadFromFile(List<String> targetList) {
        targetList.clear();
        targetList.addAll(getAllUsers());
    }

    private void append(String line) {
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get(file),
                StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            w.write(line);
            w.newLine();
        } catch (IOException ignored) {}
    }

    private void ensureHeader() {
        try {
            ensureParentDir();
            Path path = Paths.get(file);
            if (!Files.exists(path) || Files.size(path) == 0) {
                Files.write(path, Arrays.asList(row("ID", "NAME", "PHONE", "EMAIL")),
                        StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException ignored) {}
    }

    private String row(String id, String name, String phone, String email) {
        return new StringBuilder()
                .append("| ").append(pad(id, 12)).append(" | ")
                .append(pad(name, 22)).append(" | ")
                .append(pad(phone, 16)).append(" | ")
                .append(pad(email, 28)).append(" |")
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
            Path parent = Paths.get(file).getParent();
            if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
        } catch (IOException ignored) {}
    }
}

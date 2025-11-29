package library_system.infrastructure.persistence;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import library_system.application.BorrowLedger;

public class FileBorrowLedger implements BorrowLedger {
    private static final String FILE = "DATA/BORROWS.TXT";
    private static final int W_TYPE = 8;
    private static final int W_ISBN = 12;
    private static final int W_USER = 12;
    private static final int W_DUE = 12;

    @Override
    public void recordBorrow(String isbn, String userId, LocalDate dueDate) {
        if (isbn == null || userId == null || dueDate == null) return;
        ensureHeader();
        append(row("BORROW", isbn, userId, dueDate.toString()));
    }

    @Override
    public void recordReturn(String isbn) {
        if (isbn == null) return;
        ensureHeader();
        append(row("RETURN", isbn, "", ""));
    }

    @Override
    public List<String> findAll() {
        ensureParentDir();
        List<String> lines = new ArrayList<>();
        if (!Files.exists(Paths.get(FILE))) return lines;
        try {
            lines = Files.readAllLines(Paths.get(FILE));
        } catch (IOException e) {
            System.out.println("Error reading borrow ledger: " + e.getMessage());
        }
        return lines;
    }

    public void reloadFromFile(List<String> targetList) {
        targetList.clear();
        targetList.addAll(findAll());
    }

    public void deleteAll() {
        try {
            ensureParentDir();
            Files.deleteIfExists(Paths.get(FILE));
            ensureHeader();
        } catch (IOException e) {
            System.out.println("Error deleting borrow ledger file.");
        }
    }

    private String header() {
        return row("TYPE", "ISBN", "USER_ID", "DUE_DATE");
    }

    private void ensureHeader() {
        try {
            ensureParentDir();
            Path path = Paths.get(FILE);
            if (!Files.exists(path) || Files.size(path) == 0) {
                Files.write(path, Arrays.asList(header()), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            }
        } catch (IOException ignored) {}
    }

    private String row(String type, String isbn, String user, String due) {
        return new StringBuilder()
                .append("| ").append(pad(type, W_TYPE)).append(" | ")
                .append(pad(isbn, W_ISBN)).append(" | ")
                .append(pad(user, W_USER)).append(" | ")
                .append(pad(due, W_DUE)).append(" |")
                .toString();
    }

    private String pad(String s, int w) {
        if (s == null) s = "";
        if (s.length() > w) return s.substring(0, w);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < w) sb.append(' ');
        return sb.toString();
    }

    private void append(String line) {
        try {
            ensureParentDir();
            try (BufferedWriter w = Files.newBufferedWriter(Paths.get(FILE),
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
                w.write(line);
                w.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing borrow ledger file.");
        }
    }

    private void ensureParentDir() {
        try {
            Path p = Paths.get(FILE);
            Path parent = p.getParent();
            if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
        } catch (IOException ignored) {}
    }
}

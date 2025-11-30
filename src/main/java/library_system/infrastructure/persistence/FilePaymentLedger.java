package library_system.infrastructure.persistence;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import library_system.application.PaymentLedger;

public class FilePaymentLedger implements PaymentLedger {
    private static final String FILE = "DATA/PAYMENTS.TXT";
    private static final int W_USER = 12;
    private static final int W_AMT = 10;
    private static final int W_DATE = 12;

    @Override
    public void recordPayment(String userId, double amount, LocalDate date) {
        if (userId == null || userId.trim().isEmpty() || date == null) return;
        ensureHeader();
        String amt = String.format("%.2f", amount);
        append(row(userId, amt, date.toString()));
    }

    @Override
    public List<String> findAll() {
        ensureParentDir();
        List<String> lines = new ArrayList<>();
        if (!Files.exists(Paths.get(FILE))) return lines;
        try {
            lines = Files.readAllLines(Paths.get(FILE));
        } catch (IOException e) {
            System.out.println("Error reading payments file: " + e.getMessage());
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
            System.out.println("Error deleting payments file.");
        }
    }

    private String header() {
        return row("USER_ID", "AMOUNT", "DATE");
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

    private String row(String user, String amount, String date) {
        return new StringBuilder()
                .append("| ").append(pad(user, W_USER)).append(" | ")
                .append(pad(amount, W_AMT)).append(" | ")
                .append(pad(date, W_DATE)).append(" |")
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
            System.out.println("Error writing payments file.");
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

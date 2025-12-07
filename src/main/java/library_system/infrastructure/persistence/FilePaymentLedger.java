package library_system.infrastructure.persistence;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;
import library_system.application.PaymentLedger;

public class FilePaymentLedger implements PaymentLedger {

    private final String file;

    private static final int W_USER = 12;
    private static final int W_AMT  = 10;
    private static final int W_DATE = 12;

    public FilePaymentLedger() {
        this("DATA/PAYMENTS.TXT");
    }

    public FilePaymentLedger(String file) {
        this.file = file;
    }

    @Override
    public void recordPayment(String userId, double amount, LocalDate date) {
        if (userId == null || userId.trim().isEmpty() || date == null) return;

        ensureParentDir();
        ensureHeaderExists();
        append(row(userId.trim(), String.format("%.2f", amount), date.toString()));
    }

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

    public void deleteAll() {
        ensureParentDir();
        try {
            Files.deleteIfExists(Paths.get(file));
            ensureHeaderExists();
        } catch (IOException ignored) {}
    }

    private String header() {
        return row("USER_ID", "AMOUNT", "DATE");
    }

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

    private String row(String user, String amount, String date) {
        return "| "
                + pad(user, W_USER) + " | "
                + pad(amount, W_AMT) + " | "
                + pad(date, W_DATE) + " |";
    }

    private String pad(String s, int w) {
        if (s == null) s = "";
        if (s.length() > w) return s.substring(0, w);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < w) sb.append(' ');
        return sb.toString();
    }

    private void append(String line) {
        try (BufferedWriter w = Files.newBufferedWriter(Paths.get(file),
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            w.write(line);
            w.newLine();
        } catch (IOException ignored) {}
    }

    private void ensureParentDir() {
        try {
            Path p = Paths.get(file);
            Path parent = p.getParent();
            if (parent != null && !Files.exists(parent)) Files.createDirectories(parent);
        } catch (IOException ignored) {}
    }
}

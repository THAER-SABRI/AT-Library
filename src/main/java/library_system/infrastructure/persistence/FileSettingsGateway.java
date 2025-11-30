package library_system.infrastructure.persistence;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import library_system.application.SettingsGateway;

public class FileSettingsGateway implements SettingsGateway {
    private static final String SETTINGS_FILE = "DATA/SETTINGS.TXT";
    private int loanDays = 28;

    @Override
    public int getLoanDays() {
        load();
        return loanDays;
    }

    @Override
    public void setLoanDays(int days) {
        if (days <= 0) return;
        loanDays = days;
        persist();
    }

    private void load() {
        ensureParentDir();
        Path path = Paths.get(SETTINGS_FILE);
        if (!Files.exists(path)) {
            persist();
            return;
        }
        try {
            for (String line : Files.readAllLines(path)) {
                if (line == null || line.trim().isEmpty()) continue;
                String[] kv = line.split("=");
                if (kv.length == 2 && kv[0].trim().equalsIgnoreCase("loanDays")) {
                    try {
                        int d = Integer.parseInt(kv[1].trim());
                        if (d > 0) loanDays = d;
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading settings file: " + e.getMessage());
        }
    }

    private void persist() {
        ensureParentDir();
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(SETTINGS_FILE),
                StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
            writer.write("loanDays=" + loanDays);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing settings file: " + e.getMessage());
        }
    }

    private void ensureParentDir() {
        try {
            Path p = Paths.get(SETTINGS_FILE);
            Path parent = p.getParent();
            if (parent != null && !Files.exists(parent)) {
                Files.createDirectories(parent);
            }
        } catch (IOException e) {
            System.out.println("Error ensuring settings directory: " + e.getMessage());
        }
    }
}

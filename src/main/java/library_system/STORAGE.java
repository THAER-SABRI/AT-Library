package library_system;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class STORAGE {
    public static final String BOOKS_FILE = "BOOKS.TXT";
    public static final String BORROWS_FILE = "BORROWS.TXT";
    public static final String PAYMENTS_FILE = "PAYMENTS.TXT";
    public static final String SETTINGS_FILE = "SETTINGS.TXT";
    public static final String USERS_FILE = "USERS.TXT";
    public static final String REPORTS_FILE = "REPORTS.TXT";

    public static void ensureFiles() {
        ensureFileWithHeader(BOOKS_FILE, "FORMAT: ISBN | TITLE | AUTHOR", "TITLE: BOOKS");
        ensureFileWithHeader(BORROWS_FILE, "FORMAT: ISBN | USER_ID | DUE_DATE(YYYY-MM-DD)", "TITLE: BORROWS");
        ensureFileWithHeader(PAYMENTS_FILE, "FORMAT: USER_ID | AMOUNT | DATE(YYYY-MM-DD)", "TITLE: PAYMENTS");
        ensureFileWithHeader(SETTINGS_FILE, "FORMAT: KEY = VALUE", "TITLE: SETTINGS");
        ensureFileWithHeader(USERS_FILE, "FORMAT: USER_ID | NAME | PHONE | EMAIL", "TITLE: USERS");
    }

    private static void ensureFileWithHeader(String name, String formatLine, String titleLine) {
        try {
            Path p = Paths.get(name);
            if (!Files.exists(p)) {
                List<String> head = new ArrayList<>();
                head.add(formatLine);
                head.add(titleLine);
                head.add("DATE: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                head.add("-----");
                Files.write(p, head, StandardOpenOption.CREATE);
            } else {
                List<String> lines = Files.readAllLines(p);
                if (lines.isEmpty()) {
                    List<String> head = new ArrayList<>();
                    head.add(formatLine);
                    head.add(titleLine);
                    head.add("DATE: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
                    head.add("-----");
                    Files.write(p, head, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                }
            }
        } catch (IOException e) {}
    }

    public static List<String> readDataLines(String name) {
        try {
            List<String> all = Files.readAllLines(Paths.get(name));
            List<String> out = new ArrayList<>();
            boolean afterSep = false;
            for (String s : all) {
                if (s == null) continue;
                String t = s.trim();
                if (!afterSep) {
                    if (t.equals("-----")) afterSep = true;
                    continue;
                }
                if (t.isEmpty()) continue;
                if (t.startsWith("#")) continue;
                out.add(s);
            }
            return out;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public static void writeDataLines(String name, List<String> lines) {
        try {
            Path p = Paths.get(name);
            List<String> out = new ArrayList<>();
            if (Files.exists(p)) {
                List<String> current = Files.readAllLines(p);
                List<String> head = new ArrayList<>();
                boolean copied = false;
                for (String s : current) {
                    head.add(s);
                    if (s.trim().equals("-----")) { copied = true; break; }
                }
                if (copied) {
                    out.addAll(head);
                    out.addAll(lines);
                } else {
                    out.addAll(lines);
                }
            } else {
                out.addAll(lines);
            }
            Files.write(p, out, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            try {
                Files.write(Paths.get(name), lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            } catch (IOException ex) {}
        }
    }

    public static void appendDataLine(String name, String line) {
        try {
            Files.write(Paths.get(name), Arrays.asList(line), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {}
    }
}
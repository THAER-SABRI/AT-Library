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

public class FileCdRepository implements CdRepository {

    private final String file;
    private final BorrowDurationStrategy borrowStrategy;
    private final FineStrategy fineStrategy;

    public FileCdRepository(String file,
                            BorrowDurationStrategy borrowStrategy,
                            FineStrategy fineStrategy) {
        this.file = file;
        this.borrowStrategy = borrowStrategy;
        this.fineStrategy = fineStrategy;
    }

    public FileCdRepository(BorrowDurationStrategy borrowStrategy,
                            FineStrategy fineStrategy) {
        this("DATA/CDS.TXT", borrowStrategy, fineStrategy);
    }

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

    private String row(String... cols) {
        return "| " + String.join(" | ", cols) + " |";
    }

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

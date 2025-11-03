package library_system;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BORROW_SERVICE {
    public boolean borrow(LIBRARY library, String isbn, String userId, LocalDate today) {
        if (library == null || isbn == null || today == null || userId == null || userId.trim().isEmpty()) return false;
        BOOK b = library.findByIsbn(isbn);
        if (b == null) return false;
        if (b.isBorrowed()) return false;
        int days = SETTINGS.getLoanDays();
        LocalDate due = today.plusDays(days);
        b.markBorrowed(userId, due);
        List<String> lines = STORAGE.readDataLines(STORAGE.BORROWS_FILE);
        boolean found = false;
        for (int i = 0; i < lines.size(); i++) {
            String[] p = lines.get(i).split("\\s*\\|\\s*", -1);
            if (p.length >= 1 && p[0].equals(isbn)) { lines.set(i, isbn+" | "+userId+" | "+due); found = true; break; }
        }
        if (!found) lines.add(isbn+" | "+userId+" | "+due);
        STORAGE.writeDataLines(STORAGE.BORROWS_FILE, lines);
        return true;
    }

    public boolean returnBook(LIBRARY library, String isbn) {
        if (library == null || isbn == null) return false;
        BOOK b = library.findByIsbn(isbn);
        if (b == null) return false;
        if (!b.isBorrowed()) return false;
        b.markReturned();
        List<String> lines = STORAGE.readDataLines(STORAGE.BORROWS_FILE);
        List<String> out = new ArrayList<>();
        for (String line : lines) {
            String[] p = line.split("\\s*\\|\\s*", -1);
            if (p.length >= 1 && p[0].equals(isbn)) continue;
            out.add(line);
        }
        STORAGE.writeDataLines(STORAGE.BORROWS_FILE, out);
        return true;
    }
}

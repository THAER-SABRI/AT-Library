package library_system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.time.LocalDate;

public class LIBRARY {
    private List<BOOK> books = new ArrayList<>();

    public LIBRARY() {
        loadBooks();
        applyBorrowFlags();
    }

    private void loadBooks() {
        books.clear();
        for (String line : STORAGE.readDataLines(STORAGE.BOOKS_FILE)) {
            String[] p = line.split("\\s*\\|\\s*", -1);
            if (p.length >= 3) books.add(new BOOK(p[1], p[2], p[0]));
        }
    }

    private void applyBorrowFlags() {
        Map<String,String[]> map = new HashMap<>();
        for (String line : STORAGE.readDataLines(STORAGE.BORROWS_FILE)) {
            String[] p = line.split("\\s*\\|\\s*", -1);
            if (p.length >= 3) map.put(p[0], p);
        }
        for (BOOK b : books) {
            if (map.containsKey(b.getIsbn())) {
                String[] v = map.get(b.getIsbn());
                String uid = v[1];
                LocalDate due = null;
                try { due = LocalDate.parse(v[2]); } catch(Exception e) { due = null; }
                b.markBorrowed(uid, due);
            } else {
                b.markReturned();
            }
        }
    }

    public boolean addBook(BOOK book) {
        if (book == null) return false;
        if (book.getTitle() == null || book.getTitle().trim().isEmpty()) return false;
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) return false;
        if (book.getIsbn() == null || book.getIsbn().trim().isEmpty()) return false;
        for (BOOK b : books) if (b.getIsbn().equals(book.getIsbn())) return false;
        books.add(book);
        STORAGE.appendDataLine(STORAGE.BOOKS_FILE, book.getIsbn()+" | "+book.getTitle()+" | "+book.getAuthor());
        return true;
    }

    public List<BOOK> search(String keyword) {
        List<BOOK> results = new ArrayList<>();
        if (keyword == null) keyword = "";
        String key = keyword.trim().toLowerCase();
        for (BOOK b : books) {
            if (b.getTitle().toLowerCase().contains(key) ||
                b.getAuthor().toLowerCase().contains(key) ||
                b.getIsbn().toLowerCase().contains(key)) {
                results.add(b);
            }
        }
        return results;
    }

    public BOOK findByIsbn(String isbn) {
        if (isbn == null) return null;
        for (BOOK b : books) if (isbn.equals(b.getIsbn())) return b;
        return null;
    }

    public boolean isAvailable(String isbn) {
        BOOK b = findByIsbn(isbn);
        return b != null && !b.isBorrowed();
    }

    public List<BOOK> getBooks() {
        return Collections.unmodifiableList(books);
    }

    public void refresh() {
        loadBooks();
        applyBorrowFlags();
    }

    public void syncBorrowFlagsFromFile() {
        applyBorrowFlags();
    }
}

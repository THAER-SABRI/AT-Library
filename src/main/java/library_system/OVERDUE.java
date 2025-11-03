package library_system;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OVERDUE {
    public static List<BOOK> list(LIBRARY library, LocalDate today) {
        List<BOOK> r = new ArrayList<>();
        for (BOOK b : library.getBooks()) {
            if (b.isBorrowed() && b.getDueDate() != null && today.isAfter(b.getDueDate())) r.add(b);
        }
        return r;
    }

    public static boolean isOverdue(BOOK book, LocalDate today) {
        if (book == null || book.getDueDate() == null) return false;
        return today.isAfter(book.getDueDate());
    }
}

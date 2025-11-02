package library_system;

import java.util.ArrayList;
import java.util.List;

public class LIBRARY {
    private List<BOOK> books = new ArrayList<>();

    public boolean addBook(BOOK book) {
        if(book == null) return false;
        if(book.getTitle() == null || book.getTitle().trim().isEmpty()) return false;
        if(book.getAuthor() == null || book.getAuthor().trim().isEmpty()) return false;
        if(book.getIsbn() == null || book.getIsbn().trim().isEmpty()) return false;
        books.add(book);
        return true;
    }

    public void searchBook(String keyword) {
        if(keyword == null || keyword.trim().isEmpty()) {
            System.out.println(" Invalid search keyword.");
            return;
        }
        List<BOOK> results = new ArrayList<>();
        String key = keyword.trim().toLowerCase();
        for(BOOK b : books) {
            if(b.getTitle().toLowerCase().contains(key) ||
               b.getAuthor().toLowerCase().contains(key) ||
               b.getIsbn().toLowerCase().contains(key)) {
                results.add(b);
            }
        }
        if(results.isEmpty()) {
            System.out.println(" No matching books found.");
            return;
        }

        System.out.println("╔═══════════════════════════════════════════════════════════════╗");
        System.out.printf("║ %-20s │ %-20s │ %-15s ║\n", "TITLE", "AUTHOR", "ISBN");
        System.out.println("╠═══════════════════════════════════════════════════════════════╣");
        for(BOOK b : results) {
            System.out.printf("║ %-20s │ %-20s │ %-15s ║\n",
                    b.getTitle(), b.getAuthor(), b.getIsbn());
        }
        System.out.println("╚═══════════════════════════════════════════════════════════════╝");
    }
}

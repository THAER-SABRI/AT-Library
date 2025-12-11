package library_system.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.Media;

public class OverdueMedia {

    private final BookRepository bookRepo;
    private final CdRepository cdRepo;

    public OverdueMedia(BookRepository bookRepo, CdRepository cdRepo) {
        if (bookRepo == null || cdRepo == null)
            throw new IllegalArgumentException("Repositories cannot be null");

        this.bookRepo = bookRepo;
        this.cdRepo = cdRepo;
    }

    public List<Media> getAllOverdues(LocalDate today) {
        List<Media> result = new ArrayList<>();

        for (Book b : bookRepo.getAll()) {
            if (isOverdue(b, today)) {
                result.add(b);
            }
        }

        for (CD cd : cdRepo.getAll()) {
            if (cd.isBorrowed() && cd.getDueDate() != null) {
                if (cd.getDueDate().isBefore(today)) {
                    result.add(cd);
                }
            }
        }

        return result;
    }

    private boolean isOverdue(Media m, LocalDate today) {
        if (!m.isBorrowed()) return false;
        if (m.getDueDate() == null) return false;
        return m.getDueDate().isBefore(today);
    }

    public List<Media> getOverdueForUser(String userId, LocalDate today) {
        List<Media> result = new ArrayList<>();
        if (userId == null) return result;

        for (Media m : getAllOverdues(today)) {
            if (userId.equals(m.getBorrowerId())) {
                result.add(m);
            }
        }
        return result;
    }

    public boolean userHasOverdues(String userId, LocalDate today) {
        return !getOverdueForUser(userId, today).isEmpty();
    }
}

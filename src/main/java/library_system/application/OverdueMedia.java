package library_system.application;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import library_system.domain.Book;
import library_system.domain.CD;
import library_system.domain.Media;

/**
 * Service responsible for identifying overdue media items (Books and CDs)
 * across the library system.
 * <p>
 * This class checks both books and CDs stored in their respective repositories
 * and determines whether they are overdue relative to a provided date.
 * It can also filter overdue media items for a specific user.
 * </p>
 */
public class OverdueMedia {

    private final BookRepository bookRepo;
    private final CdRepository cdRepo;

    /**
     * Creates a new {@code OverdueMedia} service.
     *
     * @param bookRepo repository providing access to all book records; must not be {@code null}
     * @param cdRepo   repository providing access to all CD records; must not be {@code null}
     *
     * @throws IllegalArgumentException if either repository is {@code null}
     */
    public OverdueMedia(BookRepository bookRepo, CdRepository cdRepo) {
        if (bookRepo == null || cdRepo == null)
            throw new IllegalArgumentException("Repositories cannot be null");

        this.bookRepo = bookRepo;
        this.cdRepo = cdRepo;
    }

    /**
     * Retrieves all overdue media items (books and CDs) as of the specified date.
     * <p>
     * An item is considered overdue if:
     * <ul>
     *     <li>It is currently borrowed</li>
     *     <li>It has a non-null due date</li>
     *     <li>The due date is before the given {@code today} date</li>
     * </ul>
     *
     * @param today the reference date for overdue calculation; must not be {@code null}
     *
     * @return a list of all overdue {@link Media} items; never {@code null}
     */
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

    /**
     * Checks whether a given media item is overdue.
     *
     * @param m     the media item to evaluate; must not be {@code null}
     * @param today the reference date used to determine overdue status
     *
     * @return {@code true} if the media is overdue; {@code false} otherwise
     */
    private boolean isOverdue(Media m, LocalDate today) {
        if (!m.isBorrowed()) return false;
        if (m.getDueDate() == null) return false;
        return m.getDueDate().isBefore(today);
    }

    /**
     * Retrieves all overdue media items borrowed by a specific user.
     *
     * @param userId the ID of the user; may be {@code null}
     * @param today  the reference date used to determine overdue status
     *
     * @return a list of overdue media items belonging to the user;
     *         never {@code null} but may be empty
     */
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

    /**
     * Checks whether a specific user has any overdue media items.
     *
     * @param userId the ID of the user; may be {@code null}
     * @param today  the reference date used for overdue calculation
     *
     * @return {@code true} if the user has at least one overdue item;
     *         {@code false} otherwise
     */
    public boolean userHasOverdues(String userId, LocalDate today) {
        return !getOverdueForUser(userId, today).isEmpty();
    }
}

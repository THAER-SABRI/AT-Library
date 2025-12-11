package library_system.application;

import java.time.LocalDate;
import java.util.List;
import library_system.domain.Admin;
import library_system.domain.Book;
import library_system.infrastructure.persistence.FileBookRepository;

/**
 * Service responsible for unregistering (removing) a user from the library system.
 * <p>
 * A user can be unregistered only under the following conditions:
 * <ul>
 *     <li>The admin is currently logged in</li>
 *     <li>The user does not have any borrowed books</li>
 *     <li>The user does not have any outstanding unpaid fines</li>
 *     <li>All input parameters are valid</li>
 * </ul>
 * <p>
 * If all conditions are met, the user is removed via {@link UserDirectory#removeUser(String)}.
 * </p>
 */
public class UnregisterUser {

    private final UserDirectory users;
    private final BookRepository repo;
    private final ComputeFine computeFine;
    private final Admin admin;

    /**
     * Creates a new {@code UnregisterUser} service.
     *
     * @param users        the directory managing user records; must not be {@code null}
     * @param bookRepo     repository of all book records; must not be {@code null}
     * @param computeFine  service used to verify any outstanding fines for a user; must not be {@code null}
     * @param admin        the admin object used to verify administrative privileges; must not be {@code null}
     *
     * @throws IllegalArgumentException if any dependency is {@code null}
     */
    public UnregisterUser(UserDirectory users, BookRepository bookRepo,
                          ComputeFine computeFine, Admin admin) {
        if (users == null || bookRepo == null || computeFine == null || admin == null)
            throw new IllegalArgumentException("dependencies cannot be null");
        this.users = users;
        this.repo = bookRepo;
        this.computeFine = computeFine;
        this.admin = admin;
    }

    /**
     * Attempts to unregister a user from the library system.
     * <p>
     * The method performs several checks:
     * <ol>
     *     <li>Validates input parameters</li>
     *     <li>Ensures an admin is logged in</li>
     *     <li>Ensures the user has no currently borrowed books</li>
     *     <li>Ensures the user has no unpaid outstanding fines</li>
     * </ol>
     * If all checks pass, the user is removed from the {@link UserDirectory}.
     *
     * @param userId the ID of the user to unregister; must not be {@code null} or empty
     * @param today  the current date used to evaluate outstanding fines; must not be {@code null}
     *
     * @return {@code true} if the user was successfully unregistered;
     *         {@code false} if any condition fails
     */
    public boolean execute(String userId, LocalDate today) {
        if (userId == null || userId.trim().isEmpty() || today == null) return false;
        if (!admin.isLoggedIn()) return false;

        // Cannot unregister a user who still has borrowed books
        List<Book> books = repo.getAll();
        for (Book b : books) {
            if (b.isBorrowed() && userId.equals(b.getBorrowerId())) {
                return false;
            }
        }

        // Cannot unregister a user who still owes money
        double outstanding = computeFine.computeOutstanding(userId, today);
        if (outstanding > 0.0) return false;

        // All checks passed → remove user
        return users.removeUser(userId);
    }
}

package library_system.application;

import java.time.LocalDate;
import java.util.List;
import library_system.domain.Admin;
import library_system.domain.Book;
import library_system.infrastructure.persistence.FileBookRepository;

public class UnregisterUser {

    private final UserDirectory users;
    private final BookRepository repo;
    private final ComputeFine computeFine;
    private final Admin admin;

    public UnregisterUser(UserDirectory users, BookRepository bookRepo,
                          ComputeFine computeFine, Admin admin) {
        if (users == null || bookRepo == null || computeFine == null || admin == null)
            throw new IllegalArgumentException("dependencies cannot be null");
        this.users = users;
        this.repo = bookRepo;
        this.computeFine = computeFine;
        this.admin = admin;
    }

    public boolean execute(String userId, LocalDate today) {
        if (userId == null || userId.trim().isEmpty() || today == null) return false;
        if (!admin.isLoggedIn()) return false;

        List<Book> books = repo.getAll();
        for (Book b : books) {
            if (b.isBorrowed() && userId.equals(b.getBorrowerId())) {
                return false;
            }
        }

        double outstanding = computeFine.computeOutstanding(userId, today);
        if (outstanding > 0.0) return false;

        return users.removeUser(userId);
    }
}

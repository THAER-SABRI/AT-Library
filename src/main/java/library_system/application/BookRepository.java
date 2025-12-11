package library_system.application;

import java.util.List;
import library_system.domain.Book;

/**
 * Repository interface for managing persistent storage of {@link Book} entities.
 * <p>
 * This interface defines the basic operations for retrieving and saving
 * a collection of books. Implementations may store books in files,
 * databases, or in-memory structures depending on the system configuration.
 * </p>
 */
public interface BookRepository {

    /**
     * Retrieves all books from the repository.
     *
     * @return a list containing all {@link Book} objects stored in the repository;
     *         never {@code null}, but may be empty.
     */
    List<Book> getAll();

    /**
     * Persists the provided list of books to the repository.
     * <p>
     * Implementations should overwrite existing data with the new list,
     * ensuring the repository remains synchronized with the application's
     * state after operations such as borrowing or returning books.
     * </p>
     *
     * @param books the list of {@link Book} objects to save; must not be {@code null}.
     */
    void saveAll(List<Book> books);
}

package library_system.application;

import java.util.List;
import library_system.domain.CD;

/**
 * Repository interface for managing persistent storage of {@link CD} entities.
 * <p>
 * Implementations of this interface are responsible for retrieving and saving
 * collections of CD objects. The storage mechanism may vary (files, databases,
 * or in-memory structures), but the behavior defined here remains consistent
 * across implementations.
 * </p>
 */
public interface CdRepository {

    /**
     * Retrieves all CD items stored in the repository.
     *
     * @return a list of all {@link CD} objects; never {@code null}, but may be empty
     */
    List<CD> getAll();

    /**
     * Saves the entire list of CDs to the repository.
     * <p>
     * Implementations typically replace existing storage content with the
     * supplied list to maintain synchronization with the application's state.
     * </p>
     *
     * @param cds the list of {@link CD} objects to persist; must not be {@code null}
     */
    void saveAll(List<CD> cds);
}

package library_system.application;

import java.util.List;

/**
 * Directory interface for managing user accounts within the library system.
 * <p>
 * Implementations of this interface handle storing, retrieving, and removing
 * user records, as well as accessing user-related information such as email
 * addresses.
 * </p>
 */
public interface UserDirectory {

    /**
     * Retrieves a list of all registered user IDs.
     *
     * @return a list containing the IDs of all users; never {@code null}
     */
    List<String> getAllUsers();

    /**
     * Adds a new user to the directory.
     *
     * @param id    the unique identifier for the user; must not be {@code null} or empty
     * @param name  the user's name; must not be {@code null}
     * @param phone the user's phone number; may be {@code null} or empty
     * @param email the user's email address; may be {@code null} or empty
     */
    void addUser(String id, String name, String phone, String email);

    /**
     * Removes a user from the directory.
     *
     * @param id the ID of the user to remove; must not be {@code null}
     * @return {@code true} if the user was successfully removed;
     *         {@code false} if the user does not exist or could not be deleted
     */
    boolean removeUser(String id);

    /**
     * Retrieves the email address associated with a specific user.
     *
     * @param userId the ID of the user; must not be {@code null}
     * @return the user's email address, or {@code null} if none is stored
     */
    String getEmail(String userId);
}

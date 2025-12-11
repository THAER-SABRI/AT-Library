package library_system.domain.events;

/**
 * Marker interface representing a domain event within the library system.
 * <p>
 * A {@code DomainEvent} captures a meaningful action or state change,
 * such as borrowing or returning a book. Implementations define specific
 * event types and carry any required contextual data.
 * </p>
 */
public interface DomainEvent {

    /**
     * Returns the unique name of the event.
     * <p>
     * This is typically used for logging, auditing,
     * or distinguishing between event types.
     * </p>
     *
     * @return the event name as a string
     */
    String getName();
}

package library_system.domain.events;

/**
 * Interface representing an observer in the Observer pattern.
 * <p>
 * Implementations of this interface react to domain events dispatched
 * by an {@link Observable}. Each observer decides how to process or
 * respond to the event.
 * </p>
 */
public interface Observer {

    /**
     * Handles an incoming domain event.
     *
     * @param event the event being delivered; must not be {@code null}
     */
    void onEvent(DomainEvent event);
}

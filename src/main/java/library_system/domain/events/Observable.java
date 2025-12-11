package library_system.domain.events;

/**
 * Interface representing the observable side of the Observer pattern.
 * <p>
 * Classes implementing this interface allow observers to register
 * and receive notifications when specific domain events occur.
 * </p>
 */
public interface Observable {

    /**
     * Registers an observer that will receive future event notifications.
     *
     * @param observer the observer to register; must not be {@code null}
     */
    void registerObserver(Observer observer);

    /**
     * Notifies all registered observers of the given domain event.
     *
     * @param event the event to broadcast; must not be {@code null}
     */
    void notifyObservers(DomainEvent event);
}

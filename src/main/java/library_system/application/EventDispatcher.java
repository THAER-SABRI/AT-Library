package library_system.application;

import library_system.domain.events.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Dispatcher responsible for managing and notifying observers about domain events.
 * <p>
 * This class implements the {@link Observable} interface and maintains a list of
 * registered observers. Whenever a {@link DomainEvent} occurs, the dispatcher
 * notifies each observer by calling {@link Observer#onEvent(DomainEvent)}.
 * </p>
 */
public class EventDispatcher implements Observable {

    private final List<Observer> observers = new ArrayList<>();

    /**
     * Registers a new observer that will receive future domain events.
     *
     * @param observer the observer to register; must not be {@code null}
     */
    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    /**
     * Notifies all registered observers of the given domain event.
     * <p>
     * Each observer receives the event through its {@link Observer#onEvent(DomainEvent)}
     * method. Observers decide whether to handle or ignore the event based on event type.
     * </p>
     *
     * @param event the domain event to broadcast; must not be {@code null}
     */
    @Override
    public void notifyObservers(DomainEvent event) {
        for (Observer o : observers) {
            o.onEvent(event);
        }
    }
}

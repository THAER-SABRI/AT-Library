package library_system.domain.events;

public interface Observable {
    void registerObserver(Observer observer);
    void notifyObservers(DomainEvent event);
}

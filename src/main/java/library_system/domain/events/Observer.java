package library_system.domain.events;

public interface Observer {
    void onEvent(DomainEvent event);
}

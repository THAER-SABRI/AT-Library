package FakeImplementationForNeededInterfaces;

import library_system.application.EventDispatcher;
import library_system.domain.events.*;

public class FakeDispatcher extends EventDispatcher {

    public DomainEvent lastEvent;

    @Override
    public void registerObserver(Observer observer) { }

    @Override
    public void notifyObservers(DomainEvent event) {
        lastEvent = event;
    }
}

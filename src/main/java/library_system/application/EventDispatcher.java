package library_system.application;

import library_system.domain.events.*;
import java.util.ArrayList;
import java.util.List;

public class EventDispatcher implements Observable {

    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void notifyObservers(DomainEvent event) {
        for (Observer o : observers) {
            o.onEvent(event);
        }
    }
}

package pl.edu.pwr.pwrinspace.poliwrocket.Event;

import java.util.ArrayList;
import java.util.EventListener;
import java.util.List;

public abstract class EventEmitter<T extends  EventListener> {
    protected final List<T> listeners = new ArrayList<>();

    public void addListener(T toAdd){
        listeners.add(toAdd);
    }

    public abstract void emit();
}

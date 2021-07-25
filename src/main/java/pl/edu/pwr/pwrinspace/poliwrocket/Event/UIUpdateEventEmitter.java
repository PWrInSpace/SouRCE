package pl.edu.pwr.pwrinspace.poliwrocket.Event;

public class UIUpdateEventEmitter extends EventEmitter<IUIUpdateEventListener> {

    @Override
    public void emit() {
        listeners.forEach(IUIUpdateEventListener::onUIUpdateEvent);
    }
}

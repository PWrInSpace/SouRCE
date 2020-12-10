package pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;

import javafx.beans.InvalidationListener;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensorRepository;

public abstract class BaseMessageParser implements IMessageParser {

    protected final ISensorRepository sensorRepository;

    protected String lastMessage;

    private InvalidationListener observer;

    public BaseMessageParser(ISensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    public String getLastMessage() {
        return lastMessage;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        this.observer = invalidationListener;
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        if(observer != null){
            observer = null;
        }
    }

    protected void notifyObserver(){
        if(observer!=null){
            observer.invalidated(this);
        }
    }
}

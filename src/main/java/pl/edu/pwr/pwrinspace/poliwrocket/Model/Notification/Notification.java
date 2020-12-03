package pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification;

import javafx.beans.InvalidationListener;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationFormatService;

import java.util.ArrayList;
import java.util.List;

public abstract class Notification implements INotification {

    private List<InvalidationListener> observers = new ArrayList<>();

    protected NotificationFormatService notificationFormatService;

    public Notification(NotificationFormatService notificationFormatService) {
        this.notificationFormatService = notificationFormatService;
    }

    @Override
    public synchronized void setup() {
        notifyObserver();
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        observers.remove(invalidationListener);
    }

    private void notifyObserver() {
        for (InvalidationListener obs : observers) {
            obs.invalidated(this);
        }
    }}

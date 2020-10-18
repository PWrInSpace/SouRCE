package pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification;

import javafx.beans.Observable;

public interface INotification extends Observable {
    void sendNotification(String message);
    void setup();
    boolean isConnected();
}

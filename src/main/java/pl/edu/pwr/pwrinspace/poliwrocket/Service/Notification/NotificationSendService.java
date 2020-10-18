package pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.INotification;

public class NotificationSendService {

    private INotification notification;

    public NotificationSendService(INotification notification) {
        this.notification = notification;
    }

    public void sendNotification(String message){
        notification.sendNotification(message);
    }

    public boolean isConnected() {
        return notification.isConnected();
    }
}

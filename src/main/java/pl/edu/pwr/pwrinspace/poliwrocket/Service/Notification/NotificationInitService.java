package pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.INotification;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.InitService;

public class NotificationInitService implements InitService {

    private INotification notification;

    public NotificationInitService(INotification notification) {
        this.notification = notification;
    }

    @Override
    public void setup() {
        notification.setup();
    }
}

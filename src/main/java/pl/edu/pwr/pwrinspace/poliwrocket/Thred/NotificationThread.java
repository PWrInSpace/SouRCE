package pl.edu.pwr.pwrinspace.poliwrocket.Thred;

import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationSendService;

import java.util.HashMap;

public class NotificationThread implements INotificationThread {

    private HashMap<String, Integer> schedule;

    private NotificationSendService notificationSendService;

    private int seconds = 1;

    public NotificationThread(NotificationSendService notificationSendService) {
        this.schedule = new HashMap<>();
        this.notificationSendService = notificationSendService;
    }

    @Override
    public void sendNotifications(String message) {
        notificationSendService.sendNotification(message);
    }

    @Override
    public void setupSchedule(HashMap<String, Integer> schedule) {
        this.schedule = schedule;
    }

    @Override
    public String getFormattedMessage(String messageKey) {
        return messageKey;
    }

    @Override
    public void run() {
        if (!schedule.isEmpty()) {
            try {
                while (true) {
                    schedule.forEach((messageKey, forEachSeconds) -> {
                        if (seconds % forEachSeconds == 0) {
                            sendNotifications(getFormattedMessage(messageKey));
                        }
                    });

                    Thread.sleep(1000);

                    seconds++;
                    if (seconds == Integer.MIN_VALUE) {
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }
    }

}

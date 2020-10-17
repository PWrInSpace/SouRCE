package pl.edu.pwr.pwrinspace.poliwrocket.Thred;

import java.util.HashMap;

public interface INotificationThread extends Runnable {
    void sendNotifications(String message);
    void setupSchedule(HashMap<String, Integer> schedule);
    String getFormattedMessage(String messageKey);
}

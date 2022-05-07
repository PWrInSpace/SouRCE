package pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification;

public abstract class NotificationFormatService {

    protected String googleMap = "https://www.google.pl/maps/dir//";

    public abstract Object getFormattedMessage(String messageKey);

}

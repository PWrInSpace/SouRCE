package pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Schedule {

    @JsonProperty("messageKey")
    private String messageKey;

    @JsonProperty("everySecond")
    private int everySecond;

    public Schedule(String messageKey, int everySecond) {
        this.messageKey = messageKey;
        this.everySecond = everySecond;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public int getEverySecond() {
        return everySecond;
    }
}

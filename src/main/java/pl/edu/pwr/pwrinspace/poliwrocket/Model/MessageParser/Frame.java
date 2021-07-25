package pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;

import java.time.Instant;

public class Frame {

    private final String content;

    private String formattedContent;

    private final Instant timeInstant;

    private String key;

    public Frame(String content, Instant timeInstant) {
        this.content = content;
        this.timeInstant = timeInstant;
    }

    public String getContent() {
        return content;
    }

    public String getFormattedContent() {
        return formattedContent;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setFormattedContent(String formattedContent) {
        this.formattedContent = formattedContent;
    }

    public Instant getTimeInstant() {
        return timeInstant;
    }
}

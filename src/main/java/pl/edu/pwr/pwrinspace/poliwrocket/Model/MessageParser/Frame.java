package pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;

import java.time.Instant;

public class Frame {

    private String content;

    private String formattedContent;

    private Instant timeInstant;

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

    public void setFormattedContent(String formattedContent) {
        this.formattedContent = formattedContent;
    }

    public Instant getTimeInstant() {
        return timeInstant;
    }
}

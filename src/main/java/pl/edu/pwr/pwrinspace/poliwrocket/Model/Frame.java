package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import java.time.Instant;

public class Frame {

    private String content;

    private Instant timeInstant;

    public Frame(String content, Instant timeInstant) {
        this.content = content;
        this.timeInstant = timeInstant;
    }

    public String getContent() {
        return content;
    }

    public Instant getTimeInstant() {
        return timeInstant;
    }
}

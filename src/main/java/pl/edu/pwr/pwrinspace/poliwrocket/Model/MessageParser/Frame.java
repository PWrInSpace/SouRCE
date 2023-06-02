package pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;

import java.time.Instant;

public class Frame {

    private final String stringContent;
    private final byte[] byteContent;

    private String formattedContent;

    private final Instant timeInstant;

    private String key;

    public Frame(byte[] buffer, Instant timeInstant) {
        this.stringContent = new String(buffer);
        this.byteContent = buffer;
        this.timeInstant = timeInstant;
    }

    public String getStringContent() {
        return stringContent;
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

    public byte[] getByteContent() {
        return byteContent;
    }
}

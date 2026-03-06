package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Content;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BaseProtobufContent {
    @JsonProperty("command")
    private String command;

    public String getCommand() {
        return command;
    }

    @Override
    public String toString() {
        return "command: " + command;
    }

    protected void setCommand(String command) {
        this.command = command;
    }
}

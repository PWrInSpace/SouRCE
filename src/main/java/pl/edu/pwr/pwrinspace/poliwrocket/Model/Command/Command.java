package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;

import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY
)
public abstract class Command<T> implements ICommand {
    @JsonProperty("value")
    protected T value;
    @JsonProperty("isFinal")
    private boolean isFinal;
    @JsonProperty("trigger")
    private String trigger;
    @JsonProperty("description")
    private String description;
    @JsonProperty("payload")
    protected String payload = "";
    @JsonProperty("commandType")
    protected String commandType = CommandType.INPUT_COMMAND.toString();
    @JsonProperty("destinationControllerNames")
    private List<String> destinationControllerNames = new ArrayList<>();

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean isFinal) {
        this.isFinal = isFinal;
    }

    @Override
    public String getCommandTriggerKey() {
        return trigger;
    }

    public void setTrigger(String trigger) {
        this.trigger = trigger;
    }

    @Override
    public String getCommandDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getPayload() {
        return payload;
    }

    @Override
    public void setPayload(String payload) {
        this.payload = payload;
    }

    @Override
    public CommandType getCommandType() {
        return CommandType.valueOf(commandType);
    }

    public void setCommandType(CommandType commandType) {
        this.commandType = commandType.toString();
    }

    public List<String> getDestinationControllerNames() {
        return destinationControllerNames;
    }

    public void setDestinationControllerNames(List<String> destinationControllerNames) {
        this.destinationControllerNames = destinationControllerNames;
    }

    @Override
    public byte[] getCommandValueAsBytes(boolean force) {
        return getCommandValueAsString().getBytes();
    }

    @Override
    public byte[] getCommandValueAsBytes() {
        return getCommandValueAsBytes(false);
    }

    @Override
    public String getCommandValueAsString() {
        return value.toString() + payload;
    }

    @Override
    public String toString() {
        return trigger;
    }

    @Override
    public int hashCode() {
        return trigger.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    public abstract String getListViewString();
}

package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command;

public interface ICommand {
    String getCommandValueAsString();
    void setPayload(String payload);
    String getCommandTriggerKey();
    String getCommandDescription();
    byte[] getCommandValueAsBytes(boolean force);
    byte[] getCommandValueAsBytes();
}

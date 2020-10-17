package pl.edu.pwr.pwrinspace.poliwrocket.Model;

public interface IMessageParser {
    void parseMessage(Frame frame);
    String getLastMessage();
}


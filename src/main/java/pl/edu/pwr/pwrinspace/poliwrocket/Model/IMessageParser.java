package pl.edu.pwr.pwrinspace.poliwrocket.Model;

public interface IMessageParser {
    void parseMessage(byte[] msg, int length);
    String getLastMessage();
}


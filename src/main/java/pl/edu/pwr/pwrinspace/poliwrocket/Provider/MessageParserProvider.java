package pl.edu.pwr.pwrinspace.poliwrocket.Provider;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.*;

public class MessageParserProvider {
    private IMessageParser messageParser;
    private MessageParserProvider() {
        if (MessageParserProvider.Holder.INSTANCE != null) {
            throw new IllegalStateException("Singleton already constructed");
        }
        init();
    }

    public void init() {
        if (Configuration.getInstance().PARSER_TYPE == MessageParserEnum.JSON) {
            messageParser = new JsonMessageParser();
        } else if (Configuration.getInstance().PARSER_TYPE == MessageParserEnum.STANDARD) {
            messageParser = new StandardMessageParser();
        }else if (Configuration.getInstance().PARSER_TYPE == MessageParserEnum.PROTOBUF) {
            messageParser = new ProtobufMessageParser();
        } else {
            messageParser = new StandardMessageParser();
        }
    }

    public IMessageParser getMessageParser() {
        return messageParser;
    }

    private static class Holder {
        private static final MessageParserProvider INSTANCE = new MessageParserProvider();
    }
}

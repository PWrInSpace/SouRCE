package pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensorRepository;

public class JsonMessageParser extends BaseMessageParser {

    private static final Logger logger = LoggerFactory.getLogger(JsonMessageParser.class);

    public JsonMessageParser(ISensorRepository sensorRepository) {
        super(sensorRepository);
    }

    //TODO adjust to new key/frame pattern
    @Override
    protected void parseInternal(Frame frame) {
        /*lastMessage = frame.getContent();
        logger.info("Message received: {}", lastMessage);
        JsonObject jsonObject = null;

        try {
            jsonObject = JsonParser.parseString(lastMessage).getAsJsonObject();
            String parsedMessage = "";

            if(Configuration.getInstance().FRAME_PATTERN.size() == jsonObject.entrySet().size()){
                for (String sensorName : Configuration.getInstance().FRAME_PATTERN) {
                    try {
                        parsedMessage += jsonObject.get(sensorName).getAsString() + Configuration.getInstance().FRAME_DELIMITER;
                        var value = Double.parseDouble(jsonObject.get(sensorName).getAsString());
                        addSensorUpdate(() -> sensorRepository.getSensorByName(sensorName).setValue(value));
                    } catch (NumberFormatException e) {
                        this.lastMessage = "Invalid: " + this.lastMessage;
                        logger.warn("Wrong message, value is not a number! {}", lastMessage + " -> " + jsonObject.get(sensorName).getAsString());
                    }
                }
                frame.setFormattedContent(parsedMessage.substring(0,parsedMessage.length()-1) + "\n");
            } else {
                parsingResultStatus = ParsingResultStatus.ERROR;
                this.lastMessage = "Invalid: " + this.lastMessage;
                var logMessage = Configuration.getInstance().FRAME_PATTERN.size() + " got: " + jsonObject.entrySet().size();
                logger.warn("Wrong message length! Expected: {}", logMessage);
                frame.setFormattedContent(lastMessage);
            }
        } catch (Exception e) {
            parsingResultStatus = ParsingResultStatus.ERROR;
            this.lastMessage = "Not valid json: " + lastMessage;
            logger.error("Not valid json: {}",lastMessage);
        }

        if(parsingResultStatus == ParsingResultStatus.PENDING) {
            parsingResultStatus = ParsingResultStatus.OK;
        }*/
        throw new UnsupportedOperationException();
    }
}

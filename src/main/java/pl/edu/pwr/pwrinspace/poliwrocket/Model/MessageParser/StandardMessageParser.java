package pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;

public class StandardMessageParser extends BaseMessageParser {

    private static final Logger logger = LoggerFactory.getLogger(StandardMessageParser.class);


    @Override
    protected void parseInternal(Frame frame) {
        lastMessage = frame.getContent();
        logger.info("Message received: {}", lastMessage);
        String[] splitted = lastMessage.split(Configuration.getInstance().FRAME_DELIMITER);
        frame.setFormattedContent(lastMessage);
        if (splitted.length > 0) {
            var framePattern = Configuration.getInstance().FRAME_PATTERN.get(splitted[0]);
            frame.setKey(splitted[0]);

            if (framePattern != null && framePattern.size() == splitted.length - 1) {
                int currentPosition = 1;
                for (String sensorName : framePattern) {
                    try {
                        if(!splitted[currentPosition].contains(":") && !splitted[currentPosition].contains("nan")) {
                            var value = Double.parseDouble(splitted[currentPosition]);
                            addSensorUpdate(() ->  {
                                try {
                                    Configuration.getInstance().sensorRepository.getSensorByName(sensorName).setValue(value);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                        }
                    } catch (NumberFormatException e) {
                        this.lastMessage = "Invalid: " + this.lastMessage;
                        logger.warn("Wrong message, value is not a number! {}", lastMessage + " -> " + splitted[currentPosition]);
                        this.parsingResultStatus = ParsingResultStatus.ERROR;
                    } finally {
                        currentPosition++;
                    }
                }
            } else {
                if(framePattern != null) {
                    var logMessage = Configuration.getInstance().FRAME_PATTERN.get(splitted[0]).size() + " got: " + (splitted.length - 1);
                    logger.warn("Wrong message length! Expected: {}", logMessage);
                } else if(splitted.length > 0){
                    logger.warn("Wrong message key {} - unrecognized.", splitted[0]);
                } else {
                    logger.warn("Wrong message, unrecognized problem. {}", lastMessage);
                }
                this.lastMessage = "Invalid: " + this.lastMessage;
                this.parsingResultStatus = ParsingResultStatus.ERROR;
            }

            if (parsingResultStatus == ParsingResultStatus.PENDING) {
                parsingResultStatus = ParsingResultStatus.OK;
            }
        }
    }
}

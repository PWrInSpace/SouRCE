package pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration;

import com.google.gson.annotations.Expose;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.BaseSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.MessageParserEnum;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppGeneralConfig extends BaseSaveModel {
    @Expose public int FPS = 10;
    @Expose public int AVERAGING_PERIOD = 1000;
    @Expose public int BUFFER_SIZE;
    @Expose public double START_POSITION_LAT = 49.013517;
    @Expose public double START_POSITION_LON = 8.404435;
    @Expose public MessageParserEnum PARSER_TYPE = MessageParserEnum.STANDARD;
    @Expose public String FRAME_DELIMITER = ",";
    @Expose public String DISCORD_TOKEN = "";
    @Expose public String DISCORD_CHANNEL_NAME = "";
    @Expose public Map<String, List<String>> FRAME_PATTERN = new HashMap<>();
    @Expose public String MSG_PREFIX = "";

    public AppGeneralConfig() {
        super(Configuration.CONFIG_PATH, "AppConfig.json");
    }
}
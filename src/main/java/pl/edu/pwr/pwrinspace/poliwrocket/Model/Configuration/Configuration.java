package pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BaseButtonSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BaseController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BaseSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.MessageParserEnum;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.Schedule;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Protobuf.ProtobufDeviceRepository;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Protobuf.ProtobufSystemRepository;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.*;

import java.time.Instant;
import java.util.*;

public class Configuration implements Observable {

    private static final Logger logger = LoggerFactory.getLogger(Configuration.class);
    public List<InvalidationListener> observers = new ArrayList<>();

    public int FPS = 10;
    public int AVERAGING_PERIOD = 1000;
    public int BUFFER_SIZE;
    public double START_POSITION_LAT = 49.013517;
    public double START_POSITION_LON = 8.404435;
    private boolean forceCommandsActive = false;
    public MessageParserEnum PARSER_TYPE = MessageParserEnum.STANDARD;

    protected static String CONFIG_PATH = "./config/";
    public static final String CONFIG_FILE_NAME = "config.json";
    public static final String FLIGHT_DATA_PATH = "./flightData/";
    public static final String FLIGHT_DATA_FILE_NAME = "Flight_" + Instant.now().getEpochSecond() + ".txt";

    public String DISCORD_TOKEN = "";
    public String DISCORD_CHANNEL_NAME = "";
    public String FRAME_DELIMITER = ",";
    public Map<String,List<String>> FRAME_PATTERN = new HashMap<>();
    public String MSG_PREFIX = "";

    public List<Command<?>> commandsList = new LinkedList<>();
    public List<Schedule> notificationSchedule = new LinkedList<>();
    public List<String> notificationMessageKeys = new LinkedList<>();

    public SensorRepository sensorRepository = new SensorRepository();
    public InterpreterRepository interpreterRepository = new InterpreterRepository();
    public ProtobufSystemRepository protobufSystemRepository = new ProtobufSystemRepository();
    public ProtobufDeviceRepository protobufDeviceRepository = new ProtobufDeviceRepository();
    public Map<String, Object> speechRules = new HashMap<>();

    public Collection<BaseController> controllersList = new LinkedList<>();
    public final static Instant startUpTime = Instant.now();
    private boolean lightMode = false;

    private Configuration() {
        if (getInstance() != null) {
            throw new IllegalStateException("Singleton already constructed");
        }
    }

    public void setLightMode(boolean lightMode) {
        this.lightMode = lightMode;
        for (InvalidationListener listener : observers) {
            listener.invalidated(this);
        }
    }

    public boolean isLightMode() {
        return lightMode;
    }

    public void setConfigPath(String path) {
        CONFIG_PATH = path;
    }

    public static String getConfigFilesPath() {
        return CONFIG_PATH;
    }

    public static String getFlightDataFileName(String key) {
        return "Flight_" + key + "_" + startUpTime.getEpochSecond() + ".txt";
    }

    public void reloadConfigInstance(AppGeneralConfig general, CommandsConfig commands, SensorsConfig sensors, InterpretersConfig interpreters, NotificationsConfig notifications, ProtobufConfig proto, SpeechConfig speech) {
        setupConfigInstance(general, commands, sensors, interpreters, notifications, proto, speech);
        setupApplicationConfig(this.controllersList);
    }

    public void setupConfigInstance(AppGeneralConfig general, CommandsConfig commands, SensorsConfig sensors, InterpretersConfig interpreters, NotificationsConfig notifications, ProtobufConfig proto, SpeechConfig speech) {
        this.FPS = general.FPS;
        this.AVERAGING_PERIOD = general.AVERAGING_PERIOD;
        this.BUFFER_SIZE = general.BUFFER_SIZE;
        this.START_POSITION_LAT = general.START_POSITION_LAT;
        this.START_POSITION_LON = general.START_POSITION_LON;
        this.PARSER_TYPE = general.PARSER_TYPE;
        this.FRAME_DELIMITER = general.FRAME_DELIMITER;
        this.FRAME_PATTERN = general.FRAME_PATTERN;
        this.DISCORD_TOKEN = general.DISCORD_TOKEN;
        this.DISCORD_CHANNEL_NAME = general.DISCORD_CHANNEL_NAME;
        this.MSG_PREFIX = general.MSG_PREFIX;

        this.commandsList = commands.commandsList;
        this.interpreterRepository = interpreters.interpreterRepository;
        this.protobufDeviceRepository = proto.protobufDeviceRepository;
        this.protobufSystemRepository = proto.protobufSystemRepository;
        this.notificationMessageKeys = notifications.notificationMessageKeys;
        this.notificationSchedule = notifications.notificationSchedule;
        this.speechRules = speech.speechRules;
        this.sensorRepository = sensors.processAndGetRepository(
                this.PARSER_TYPE,
                this.FRAME_PATTERN,
                this.interpreterRepository
        );
    }

    public void reloadConfigInstance(ConfigurationSaveModel config) {
        setupConfigInstance(config);
        setupApplicationConfig(this.controllersList);
    }

    public void setupConfigInstance(ConfigurationSaveModel config) {
        config.loadRemainingSplitFiles();
        
        AppGeneralConfig general = new AppGeneralConfig();
        general.FPS = config.FPS;
        general.AVERAGING_PERIOD = config.AVERAGING_PERIOD;
        general.BUFFER_SIZE = config.BUFFER_SIZE;
        general.START_POSITION_LAT = config.START_POSITION_LAT;
        general.START_POSITION_LON = config.START_POSITION_LON;
        general.PARSER_TYPE = config.PARSER_TYPE;
        general.FRAME_DELIMITER = config.FRAME_DELIMITER;
        general.FRAME_PATTERN = config.FRAME_PATTERN;
        general.DISCORD_TOKEN = config.DISCORD_TOKEN;
        general.DISCORD_CHANNEL_NAME = config.DISCORD_CHANNEL_NAME;
        general.MSG_PREFIX = config.MSG_PREFIX;

        CommandsConfig commands = new CommandsConfig();
        commands.commandsList = config.commandsList;

        SensorsConfig sensors = new SensorsConfig();
        sensors.sensorRepository = config.sensorRepository;

        InterpretersConfig interpreters = new InterpretersConfig();
        interpreters.interpreterRepository = config.interpreterRepository;

        NotificationsConfig notifications = new NotificationsConfig();
        notifications.notificationSchedule = config.notificationSchedule;
        notifications.notificationMessageKeys = config.notificationMessageKeys;

        ProtobufConfig proto = new ProtobufConfig();
        proto.protobufDeviceRepository = config.protobufDeviceRepository;
        proto.protobufSystemRepository = config.protobufSystemRepository;

        SpeechConfig speech = new SpeechConfig();
        speech.speechRules = config.speechRules;

        setupConfigInstance(general, commands, sensors, interpreters, notifications, proto, speech);
    }

    public void setupApplicationConfig(Collection<BaseController> controllersList) {
        this.controllersList = controllersList;

        SensorsConfig sensorsConfig = new SensorsConfig();
        sensorsConfig.sensorRepository = this.sensorRepository;
        sensorsConfig.assignSensorsToControllers(controllersList);

        CommandsConfig commandsConfig = new CommandsConfig();
        commandsConfig.commandsList = this.commandsList;
        commandsConfig.assignCommandsToControllers(controllersList);
    }

    public static Configuration getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        observers.remove(invalidationListener);
    }

    protected void notifyObserver() {
        for (InvalidationListener obs : observers) {
            obs.invalidated(this);
        }
    }

    public boolean isForceCommandsActive() {
        return forceCommandsActive;
    }

    public void setForceCommandsActive(boolean forceCommandsActive) {
        this.forceCommandsActive = forceCommandsActive;
        notifyObserver();
    }

    private static class Holder {
        private static final Configuration INSTANCE = new Configuration();
    }
}
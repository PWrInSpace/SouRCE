package pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration;

import com.google.gson.annotations.Expose;
import com.google.protobuf.Descriptors;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.BaseSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.FrameProtos;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.MessageParserEnum;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.Schedule;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Protobuf.ProtobufDeviceRepository;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Protobuf.ProtobufSystemRepository;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.*;

import java.util.*;

public class ConfigurationSaveModel extends BaseSaveModel {

    @Expose public int FPS = 10;
    @Expose public int AVERAGING_PERIOD = 1000;
    @Expose public int BUFFER_SIZE;
    @Expose public double START_POSITION_LAT;
    @Expose public double START_POSITION_LON;
    @Expose public MessageParserEnum PARSER_TYPE = MessageParserEnum.STANDARD;
    @Expose public String FRAME_DELIMITER = ",";
    @Expose public String DISCORD_TOKEN = "";
    @Expose public String DISCORD_CHANNEL_NAME = "rocket";
    @Expose public Map<String, List<String>> FRAME_PATTERN = new HashMap<>();
    @Expose public String MSG_PREFIX = "";
    @Expose public List<Command<?>> commandsList = new LinkedList<>();
    @Expose public List<Schedule> notificationSchedule = new LinkedList<>();
    @Expose public List<String> notificationMessageKeys = new LinkedList<>();
    @Expose public SensorRepository sensorRepository = new SensorRepository();
    @Expose public InterpreterRepository interpreterRepository = new InterpreterRepository();
    @Expose public ProtobufDeviceRepository protobufDeviceRepository = new ProtobufDeviceRepository();
    @Expose public ProtobufSystemRepository protobufSystemRepository = new ProtobufSystemRepository();

    public ConfigurationSaveModel() {
        super(Configuration.CONFIG_PATH, Configuration.CONFIG_FILE_NAME);
    }

    public static ConfigurationSaveModel getConfigurationSaveModel(Configuration configuration) {
        ConfigurationSaveModel config = new ConfigurationSaveModel();
        config.FPS = configuration.FPS;
        config.AVERAGING_PERIOD = configuration.AVERAGING_PERIOD;
        config.BUFFER_SIZE = configuration.BUFFER_SIZE;
        config.START_POSITION_LAT = configuration.START_POSITION_LAT;
        config.START_POSITION_LON = configuration.START_POSITION_LON;
        config.PARSER_TYPE = configuration.PARSER_TYPE;
        config.FRAME_DELIMITER = configuration.FRAME_DELIMITER;
        config.FRAME_PATTERN = configuration.FRAME_PATTERN;
        config.DISCORD_TOKEN = configuration.DISCORD_TOKEN;
        config.DISCORD_CHANNEL_NAME = configuration.DISCORD_CHANNEL_NAME;
        config.MSG_PREFIX = configuration.MSG_PREFIX;
        config.commandsList = configuration.commandsList;
        config.notificationMessageKeys = configuration.notificationMessageKeys;
        config.notificationSchedule = configuration.notificationSchedule;
        config.sensorRepository.setGpsSensor(configuration.sensorRepository.getGpsSensor());
        config.sensorRepository.setGyroSensor(configuration.sensorRepository.getGyroSensor());
        List<ISensor> partOfSensor = new ArrayList<>();
        partOfSensor.add(configuration.sensorRepository.getGpsSensor().getLatitude());
        partOfSensor.add(configuration.sensorRepository.getGpsSensor().getLongitude());
        partOfSensor.add(configuration.sensorRepository.getGyroSensor().getAxis_x());
        partOfSensor.add(configuration.sensorRepository.getGyroSensor().getAxis_y());
        partOfSensor.add(configuration.sensorRepository.getGyroSensor().getAxis_z());
        config.sensorRepository = configuration.sensorRepository;
//        configuration.sensorRepository.getSensorsKeys().forEach(s -> {
//            if(!partOfSensor.contains(configuration.sensorRepository.getSensorByName(s))){
//                config.sensorRepository.addSensor(configuration.sensorRepository.getSensorByName(s));
//            }
//        });

        config.interpreterRepository = configuration.interpreterRepository;
        ;
//        configuration.interpreterRepository.getRepositorySet().forEach((s, interpreter) -> {
//            config.interpreterRepository.addInterpreter(s,interpreter);
//        });

        config.protobufSystemRepository = configuration.protobufSystemRepository;
        config.protobufDeviceRepository = configuration.protobufDeviceRepository;

        return config;
    }

    public static ConfigurationSaveModel protobufBasedConfiguration(Configuration configuration) {
        var defaultConfig = getConfigurationSaveModel(configuration);
        defaultConfig.BUFFER_SIZE = 0;
        defaultConfig.FRAME_DELIMITER = "";
        defaultConfig.PARSER_TYPE = MessageParserEnum.PROTOBUF;
        defaultConfig.FRAME_PATTERN = new HashMap<>();
        defaultConfig.sensorRepository = new SensorRepository();
        defaultConfig.sensorRepository.setGpsSensor(configuration.sensorRepository.getGpsSensor());
        defaultConfig.sensorRepository.setGyroSensor(configuration.sensorRepository.getGyroSensor());
        List<ISensor> partOfSensor = new ArrayList<>();
        partOfSensor.add(configuration.sensorRepository.getGpsSensor().getLatitude());
        partOfSensor.add(configuration.sensorRepository.getGpsSensor().getLongitude());
        partOfSensor.add(configuration.sensorRepository.getGyroSensor().getAxis_x());
        partOfSensor.add(configuration.sensorRepository.getGyroSensor().getAxis_y());
        partOfSensor.add(configuration.sensorRepository.getGyroSensor().getAxis_z());

        FrameProtos.getDescriptor().getMessageTypes().forEach(descriptor -> {
            descriptor.getFields().forEach(fieldDescriptor -> {
                if (fieldDescriptor.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                    defaultConfig.sensorRepository.addSensor(new Sensor(fieldDescriptor.getName()));
                }
            });
        });

        return defaultConfig;
    }
}

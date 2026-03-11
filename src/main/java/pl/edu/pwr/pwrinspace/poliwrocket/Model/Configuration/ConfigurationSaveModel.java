package pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("FPS")
    public int FPS = 10;
    @JsonProperty("AVERAGING_PERIOD")
    public int AVERAGING_PERIOD = 1000;
    @JsonProperty("BUFFER_SIZE")
    public int BUFFER_SIZE;
    @JsonProperty("START_POSITION_LAT")
    public double START_POSITION_LAT;
    @JsonProperty("START_POSITION_LON")
    public double START_POSITION_LON;
    @JsonProperty("PARSER_TYPE")
    public MessageParserEnum PARSER_TYPE = MessageParserEnum.STANDARD;
    @JsonProperty("FRAME_DELIMITER")
    public String FRAME_DELIMITER = ",";
    @JsonProperty("DISCORD_TOKEN")
    public String DISCORD_TOKEN = "";
    @JsonProperty("DISCORD_CHANNEL_NAME")
    public String DISCORD_CHANNEL_NAME = "rocket";
    @JsonProperty("FRAME_PATTERN")
    public Map<String, List<String>> FRAME_PATTERN = new HashMap<>();
    @JsonProperty("MSG_PREFIX")
    public String MSG_PREFIX = "";
    @JsonProperty("commandsList")
    public List<Command<?>> commandsList = new LinkedList<>();
    @JsonProperty("notificationSchedule")
    public List<Schedule> notificationSchedule = new LinkedList<>();
    @JsonProperty("notificationMessageKeys")
    public List<String> notificationMessageKeys = new LinkedList<>();
    @JsonProperty("sensorRepository")
    public SensorRepository sensorRepository = new SensorRepository();
    @JsonProperty("interpreterRepository")
    public InterpreterRepository interpreterRepository = new InterpreterRepository();
    @JsonProperty("protobufDeviceRepository")
    public ProtobufDeviceRepository protobufDeviceRepository = new ProtobufDeviceRepository();
    @JsonProperty("protobufSystemRepository")
    public ProtobufSystemRepository protobufSystemRepository = new ProtobufSystemRepository();
    @JsonProperty("cat")
    public boolean cat = false;

    public ConfigurationSaveModel() {
        super(Configuration.CONFIG_PATH, Configuration.CONFIG_FILE_NAME);
    }

    // generuje ConfigurationSaveModel na bazie Configuration
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
        config.sensorRepository = new SensorRepository();
        config.sensorRepository.setGpsSensor(configuration.sensorRepository.getGpsSensor());
        config.sensorRepository.setGyroSensor(configuration.sensorRepository.getGyroSensor());
        for (String s : configuration.sensorRepository.getSensorsKeys()) {
            if (!configuration.sensorRepository.getSensorByName(s).isSubSensor()) {
                config.sensorRepository.addSensor(s, configuration.sensorRepository.getSensorByName(s));
            }
        }

        config.interpreterRepository = configuration.interpreterRepository;
        ;
//        configuration.interpreterRepository.getRepositorySet().forEach((s, interpreter) -> {
//            config.interpreterRepository.addInterpreter(s,interpreter);
//        });

        config.protobufSystemRepository = configuration.protobufSystemRepository;
        config.protobufDeviceRepository = configuration.protobufDeviceRepository;

        return config;
    }

    // generuje protobufBasedConfiguration na bazie Configuration
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

    // generuje domyślny Config, nie wymaga żadnych danych wejściowych
    public static ConfigurationSaveModel defaultConfiguration() {
        ConfigurationSaveModel defaultConfig = new ConfigurationSaveModel();
        defaultConfig.sensorRepository = new SensorRepository();
        defaultConfig.interpreterRepository = new InterpreterRepository();
        defaultConfig.FPS = 10;
        defaultConfig.AVERAGING_PERIOD = 1000;
        defaultConfig.PARSER_TYPE = MessageParserEnum.STANDARD;
        defaultConfig.commandsList = new LinkedList<>();
        defaultConfig.DISCORD_CHANNEL_NAME = "";
        defaultConfig.DISCORD_TOKEN = "";
        defaultConfig.MSG_PREFIX = "";
        defaultConfig.START_POSITION_LON = 16.9333977;
        defaultConfig.START_POSITION_LAT = 51.1266727;

        String DATA_CONTROLLER = "Data";
        String MORE_DATA_CONTROLLER = "MoreData";
        String MAIN_CONTROLLER = "Main";
        String MAP_CONTROLLER = "Map";
        String VALVES_CONTROLLER = "Valves";
        String CONNECTION_CONTROLLER = "Valves";
        String POWER_CONTROLLER = "Power";
        String ABORT_CONTROLLER = "Abort";
        String START_CONTROL_CONTROLLER = "StartControl";
        Sensor basicSensor = new Sensor();
        basicSensor.setName("Altitude");
        basicSensor.setMaxRange(2000);
        basicSensor.setMinRange(0);
        basicSensor.addSensorDestination(new SensorDestination("dataGauge1", "Data"));
        defaultConfig.sensorRepository.addSensor(basicSensor);

        //utworzenie 3xSensor for GYRO
        Sensor gyro1 = new Sensor();
        gyro1.setName("Gyro X");
        gyro1.addSensorDestination(new SensorDestination("dataGauge3", DATA_CONTROLLER));

        Sensor gyro2 = new Sensor();
        gyro2.setName("Gyro Y");
        gyro2.addSensorDestination(new SensorDestination("dataGauge5", DATA_CONTROLLER));

        Sensor gyro3 = new Sensor();
        gyro3.setName("Gyro Z");
        gyro3.addSensorDestination(new SensorDestination("dataGauge7", DATA_CONTROLLER));

        //nowy gyro
        GyroSensor gyroSensor = new GyroSensor(gyro1, gyro2, gyro3);
        gyroSensor.getSensorDestinations().add(new SensorDestination("", MAIN_CONTROLLER));
        defaultConfig.sensorRepository.setGyroSensor(gyroSensor);
        //--------

        //nowy gps
        Sensor latitude = new Sensor();
        latitude.setName("lat");
        Sensor longitude = new Sensor();
        longitude.setName("long");

        GPSSensor gpsSensor = new GPSSensor(latitude, longitude);
        gpsSensor.getSenorDestinations().add(new SensorDestination("", MAP_CONTROLLER));
        defaultConfig.sensorRepository.setGpsSensor(gpsSensor);
        //--------

        //filling level
        FillingLevelSensor fillingLevelSensor = new FillingLevelSensor();
        fillingLevelSensor.setName("N02Level");
        AlertSensor hall1 = new AlertSensor();
        AlertSensor hall2 = new AlertSensor();
        AlertSensor hall3 = new AlertSensor();
        AlertSensor hall4 = new AlertSensor();
        AlertSensor hall5 = new AlertSensor();
        fillingLevelSensor.setHallSensor1(hall1);
        fillingLevelSensor.setHallSensor2(hall2);
        fillingLevelSensor.setHallSensor3(hall3);
        fillingLevelSensor.setHallSensor4(hall4);
        fillingLevelSensor.setHallSensor5(hall5);
        defaultConfig.sensorRepository.addSensor(fillingLevelSensor);
        //--------

        ByteSensor byteSensor = new ByteSensor();
        defaultConfig.sensorRepository.addSensor(byteSensor);

        //komendy
//        Command command = new Command("open valveOpenButton1", "valveOpenButton1");
//        command.getDestinationControllerNames().add(VALVES_CONTROLLER);
//        defaultConfig.commandsList.add(command);
//        Command command2 = new Command("open valveOpenButton2", "valveOpenButton2");
//        command2.getDestinationControllerNames().add(VALVES_CONTROLLER);
//        defaultConfig.commandsList.add(command2);
//        Command command3 = new Command("open valveOpenButton3", "valveOpenButton3");
//        command3.getDestinationControllerNames().add(VALVES_CONTROLLER);
//        defaultConfig.commandsList.add(command3);
//        Command command4 = new Command("open valveOpenButton4", "valveOpenButton4");
//        command4.getDestinationControllerNames().add(VALVES_CONTROLLER);
//        defaultConfig.commandsList.add(command4);
//        Command command5 = new Command("test1", "test1");
//        command5.getDestinationControllerNames().add(CONNECTION_CONTROLLER);
//        defaultConfig.commandsList.add(command5);
//        Command command6 = new Command("test2", "test2");
//        command6.getDestinationControllerNames().add(CONNECTION_CONTROLLER);
//        defaultConfig.commandsList.add(command6);
//        Command abort = new Command("ABORT", "abortButton");
//        abort.getDestinationControllerNames().add(ABORT_CONTROLLER);
//        defaultConfig.commandsList.add(abort);
//        Command fire = new Command("FIRE", "fireButton");
//        fire.getDestinationControllerNames().add(START_CONTROL_CONTROLLER);
//        defaultConfig.commandsList.add(fire);
        //--------

        //frame
        defaultConfig.FRAME_DELIMITER = ";";
        List<String> pattern = new ArrayList<>();
        pattern.add("Gyro X");
        pattern.add("Gyro Y");
        pattern.add("Gyro Z");
        //
        defaultConfig.FRAME_PATTERN.put("PAT1", pattern)
        ;
        Sensor velocity = new Sensor();
        velocity.setName("Velocity");
        velocity.setMinRange(0);
        velocity.setMaxRange(400);
        velocity.setUnit("m/s");
        velocity.addSensorDestination(new SensorDestination("dataGauge9", MORE_DATA_CONTROLLER));
        defaultConfig.sensorRepository.addSensor(velocity);

        Sensor altitude = new Sensor();
        altitude.setName("Altitude2");
        altitude.setMinRange(0);
        altitude.setMaxRange(4500);
        altitude.setUnit("m");
        altitude.addSensorDestination(new SensorDestination("dataGauge10", MORE_DATA_CONTROLLER));
        defaultConfig.sensorRepository.addSensor(altitude);

        Sensor indicator1 = new Sensor();
        indicator1.setName("Ind 1");
        indicator1.setBoolean(true);
        indicator1.addSensorDestination(new SensorDestination("dataIndicator1", MORE_DATA_CONTROLLER));
        defaultConfig.sensorRepository.addSensor(indicator1);
        Sensor indicator2 = new Sensor();
        indicator2.setName("Ind 2");
        indicator2.setBoolean(true);
        indicator2.addSensorDestination(new SensorDestination("dataIndicator2", MORE_DATA_CONTROLLER));
        defaultConfig.sensorRepository.addSensor(indicator2);
        Sensor indicator3 = new Sensor();
        indicator3.setName("Ind 3");
        indicator3.setBoolean(true);
        indicator3.addSensorDestination(new SensorDestination("dataIndicator3", MORE_DATA_CONTROLLER));
        defaultConfig.sensorRepository.addSensor(indicator3);
        Sensor indicator4 = new Sensor();
        indicator4.setName("Ind 4");
        indicator4.setBoolean(true);
        indicator4.addSensorDestination(new SensorDestination("dataIndicator4", MORE_DATA_CONTROLLER));
        defaultConfig.sensorRepository.addSensor(indicator4);

        //notification
        List<String> notificationsListStrings = new ArrayList<>();
        notificationsListStrings.add("Map");
        notificationsListStrings.add("Position");
        notificationsListStrings.add("Data");
        notificationsListStrings.add("Max");
        notificationsListStrings.add("Thread status");
        defaultConfig.notificationMessageKeys = notificationsListStrings;

        List<Schedule> schedules = new ArrayList<>();
        schedules.add(new Schedule("Map", 5));
        schedules.add(new Schedule("Data", 10));
        defaultConfig.notificationSchedule = schedules;
        //---------------

        //power
        Sensor power1 = new Sensor();
        power1.setMaxRange(8.2);
        power1.setMinRange(7.2);
        power1.setName("Main computer");
        power1.setUnit("V");
        power1.addSensorDestination(new  SensorDestination("powerGauge1", POWER_CONTROLLER));
        defaultConfig.sensorRepository.addSensor(power1);
        Sensor power2 = new Sensor();
        power2.setMaxRange(8.2);
        power2.setMinRange(7.2);
        power2.setName("Recovery 1");
        power2.setUnit("V");
        power2.addSensorDestination(new SensorDestination("powerGauge2", POWER_CONTROLLER));
        defaultConfig.sensorRepository.addSensor(power2);
        Sensor power3 = new Sensor();
        power3.setMaxRange(8.2);
        power3.setMinRange(7.2);
        power3.setName("Recovery 2");
        power3.setUnit("V");
        power3.addSensorDestination(new  SensorDestination("powerGauge3", POWER_CONTROLLER));
        defaultConfig.sensorRepository.addSensor(power3);

        //---------------
        return defaultConfig;
    }
}

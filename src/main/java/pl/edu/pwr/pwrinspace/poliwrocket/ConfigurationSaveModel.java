package pl.edu.pwr.pwrinspace.poliwrocket;

import com.google.gson.annotations.Expose;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.ControllerNameEnum;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Schedule;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.GPSSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.GyroSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SensorRepository;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ConfigurationSaveModel {

    @Expose
    public int FPS = 10;

    @Expose
    public double START_POSITION_LAT = 49.013517;

    @Expose
    public double START_POSITION_LON = 8.404435;

    @Expose
    public String FRAME_DELIMITER = ",";

    @Expose
    public String DISCORD_TOKEN = "NzY3MDE4NjExMjM1NjE4ODM5.X4rzvw.Lt1BwdXcZhgoWyl8WC2P1VCSwos";

    @Expose
    public String DISCORD_CHANNEL_NAME = "rocket";

    @Expose
    public List<String> FRAME_PATTERN = new ArrayList<>();

    @Expose
    public List<Command> commandsListValves = new LinkedList<>();

    @Expose
    public List<Schedule> notificationSchedule = new LinkedList<>();

    @Expose
    public List<String> notificationMessageKeys = new LinkedList<>();

    @Expose
    public SensorRepository sensorRepository = new SensorRepository();

    public static ConfigurationSaveModel getConfigurationSaveModel(Configuration configuration) {
        ConfigurationSaveModel config = new ConfigurationSaveModel();
        config.FPS = configuration.FPS;
        config.START_POSITION_LAT = configuration.START_POSITION_LAT;
        config.START_POSITION_LON = configuration.START_POSITION_LON;
        config.FRAME_DELIMITER = configuration.FRAME_DELIMITER;
        config.FRAME_PATTERN = configuration.FRAME_PATTERN;
        config.DISCORD_TOKEN = configuration.DISCORD_TOKEN;
        config.DISCORD_CHANNEL_NAME = configuration.DISCORD_CHANNEL_NAME;
        config.commandsListValves = configuration.commandsListValves;
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
        configuration.sensorRepository.getSensorsKeys().forEach(s -> {
            if(!partOfSensor.contains(configuration.sensorRepository.getSensorByName(s))){
                config.sensorRepository.addSensor(configuration.sensorRepository.getSensorByName(s));
            }
        });

        return config;
    }

    public static ConfigurationSaveModel defaultConfiguration() {
        ConfigurationSaveModel defaultConfig = new ConfigurationSaveModel();
        defaultConfig.sensorRepository = new SensorRepository();
        defaultConfig.FPS = 1;
        defaultConfig.commandsListValves = new LinkedList<>();

        Sensor basicSensor = new Sensor();
        basicSensor.setDestination("dataGauge1");
        basicSensor.getDestinationControllerNames().add(ControllerNameEnum.DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(basicSensor);

        //utworzenie 3xSensor for GYRO
        Sensor gryro1 = new Sensor();
        gryro1.setDestination("dataGauge3");
        gryro1.setName("Gyro X");
        gryro1.getDestinationControllerNames().add(ControllerNameEnum.DATA_CONTROLLER);

        Sensor gryro2 = new Sensor();
        gryro2.setDestination("dataGauge5");
        gryro2.setName("Gyro Y");
        gryro2.getDestinationControllerNames().add(ControllerNameEnum.DATA_CONTROLLER);

        Sensor gryro3 = new Sensor();
        gryro3.setDestination("dataGauge7");
        gryro3.setName("Gyro Z");
        gryro3.getDestinationControllerNames().add(ControllerNameEnum.DATA_CONTROLLER);

        //nowy gryo
        GyroSensor gyroSensor = new GyroSensor(gryro1, gryro2, gryro3);
        gyroSensor.getDestinationControllerNames().add(ControllerNameEnum.MAIN_CONTROLLER);
        defaultConfig.sensorRepository.setGyroSensor(gyroSensor);
        //--------

        //nowy gps
        Sensor latitude = new Sensor();
        latitude.setName("lat");
        Sensor longitude = new Sensor();
        longitude.setName("long");

        GPSSensor gpsSensor = new GPSSensor(latitude, longitude);
        gpsSensor.getDestinationControllerNames().add(ControllerNameEnum.MAP_CONTROLLER);
        defaultConfig.sensorRepository.setGpsSensor(gpsSensor);
        //--------

        //komendy
        Command command = new Command("open valveOpenButton1", "valveOpenButton1");
        command.getDestinationControllerNames().add(ControllerNameEnum.VALVES_CONTROLLER);
        defaultConfig.commandsListValves.add(command);
        Command command2 = new Command("open valveOpenButton2", "valveOpenButton2");
        command2.getDestinationControllerNames().add(ControllerNameEnum.VALVES_CONTROLLER);
        defaultConfig.commandsListValves.add(command2);
        Command command3 = new Command("open valveOpenButton3", "valveOpenButton3");
        command3.getDestinationControllerNames().add(ControllerNameEnum.VALVES_CONTROLLER);
        defaultConfig.commandsListValves.add(command3);
        Command command4 = new Command("open valveOpenButton4", "valveOpenButton4");
        command4.getDestinationControllerNames().add(ControllerNameEnum.VALVES_CONTROLLER);
        defaultConfig.commandsListValves.add(command4);
        //--------

        //frame
        defaultConfig.FRAME_DELIMITER = ",";
        defaultConfig.FRAME_PATTERN.add("Gyro X");
        defaultConfig.FRAME_PATTERN.add("Gyro Y");
        defaultConfig.FRAME_PATTERN.add("Gyro Z");
        //

        Sensor velocity = new Sensor();
        velocity.setDestination("dataGauge9");
        velocity.setName("Velocity");
        velocity.setMinRange(0);
        velocity.setMaxRange(400);
        velocity.setUnit("m/s");
        velocity.getDestinationControllerNames().add(ControllerNameEnum.MORE_DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(velocity);

        Sensor altitude = new Sensor();
        altitude.setDestination("dataGauge10");
        altitude.setName("Altitude2");
        altitude.setMinRange(0);
        altitude.setMaxRange(4500);
        altitude.setUnit("m");
        altitude.getDestinationControllerNames().add(ControllerNameEnum.MORE_DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(altitude);

        Sensor indicator1 = new Sensor();
        indicator1.setDestination("dataIndicator1");
        indicator1.setName("Ind 1");
        indicator1.getDestinationControllerNames().add(ControllerNameEnum.MORE_DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(indicator1);
        Sensor indicator2 = new Sensor();
        indicator2.setDestination("dataIndicator2");
        indicator2.setName("Ind 2");
        indicator2.getDestinationControllerNames().add(ControllerNameEnum.MORE_DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(indicator2);
        Sensor indicator3 = new Sensor();
        indicator3.setDestination("dataIndicator3");
        indicator3.setName("Ind 3");
        indicator3.getDestinationControllerNames().add(ControllerNameEnum.MORE_DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(indicator3);
        Sensor indicator4 = new Sensor();
        indicator4.setDestination("dataIndicator4");
        indicator4.setName("Ind 4");
        indicator4.getDestinationControllerNames().add(ControllerNameEnum.MORE_DATA_CONTROLLER);
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
        schedules.add( new Schedule("Map",5));
        schedules.add( new Schedule("Data",10));
        defaultConfig.notificationSchedule = schedules;
        //---------------

        return defaultConfig;
    }
}

package pl.edu.pwr.pwrinspace.poliwrocket;

import com.google.gson.annotations.Expose;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.ControllerNameEnum;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.*;

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
    public List<Command> commandsListValves = new LinkedList<>();

    @Expose
    public SensorRepository sensorRepository = new SensorRepository();

    public static ConfigurationSaveModel getConfigurationSaveModel(Configuration configuration) {
        ConfigurationSaveModel config = new ConfigurationSaveModel();
        config.FPS = configuration.FPS;
        config.START_POSITION_LAT = configuration.START_POSITION_LAT;
        config.START_POSITION_LON = configuration.START_POSITION_LON;
        config.commandsListValves = configuration.commandsListValves;
        config.sensorRepository.setGpsSensor(configuration.sensorRepository.getGpsSensor());
        config.sensorRepository.setGyroSensor(configuration.sensorRepository.getGyroSensor());
        List<Sensor> partOfSensor = new ArrayList<>();
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

        defaultConfig.commandsListValves = new LinkedList<>();

        Sensor basicSensor = new Sensor();
        basicSensor.setDestination("dataGauge1");
        basicSensor.getDestinationControllerNames().add(ControllerNameEnum.DATA_CONTROLLER);
        defaultConfig.sensorRepository.addSensor(basicSensor);

        //utworzenie 3xSensor for GYRO
        Sensor gryro1 = new Sensor();
        gryro1.setDestination("dataGauge2");
        gryro1.setName("Gyro X");
        gryro1.getDestinationControllerNames().add(ControllerNameEnum.DATA_CONTROLLER);

        Sensor gryro2 = new Sensor();
        gryro2.setDestination("dataGauge3");
        gryro2.setName("Gyro Y");
        gryro2.getDestinationControllerNames().add(ControllerNameEnum.DATA_CONTROLLER);

        Sensor gryro3 = new Sensor();
        gryro3.setDestination("dataGauge4");
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


        return defaultConfig;
    }
}

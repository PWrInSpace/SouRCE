package pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration;

import org.javatuples.Triplet;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.MessageParserEnum;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.Schedule;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.*;

import java.time.Instant;
import java.util.*;

public class Configuration {

    public int FPS = 10;

    public int AVERAGING_PERIOD = 1000;

    public int BUFFER_SIZE;

    public double START_POSITION_LAT = 49.013517;

    public double START_POSITION_LON = 8.404435;

    public MessageParserEnum PARSER_TYPE = MessageParserEnum.STANDARD;

    public static final String CONFIG_PATH = "./config/";

    public static final String CONFIG_FILE_NAME = "config.json";

    public static final String FLIGHT_DATA_PATH = "./flightData/";

    public static final String FLIGHT_DATA_FILE_NAME = "Flight_" + Instant.now().getEpochSecond() + ".txt";

    public String DISCORD_TOKEN = "";

    public String DISCORD_CHANNEL_NAME = "";

    public String FRAME_DELIMITER = ",";

    public Map<String,List<String>> FRAME_PATTERN = new HashMap<>();

    public List<Command> commandsList = new LinkedList<>();

    public List<Schedule> notificationSchedule = new LinkedList<>();

    public List<String> notificationMessageKeys = new LinkedList<>();

    public SensorRepository sensorRepository = new SensorRepository();

    public List<BasicController> controllersList = new LinkedList<>();

    private Configuration() {
        if (Holder.INSTANCE != null) {
            throw new IllegalStateException("Singleton already constructed");
        }
    }

    public static String getFlightDataFileName(String key) {
        return "Flight_" + key + "_" + Instant.now().getEpochSecond() + ".txt";
    }

    public void reloadConfigInstance(ConfigurationSaveModel config) {
        setupConfigInstance(config);
        setupApplicationConfig(this.controllersList);
    }

    public void setupConfigInstance(ConfigurationSaveModel config) {
        this.FPS = config.FPS;
        this.AVERAGING_PERIOD = config.AVERAGING_PERIOD;
        this.BUFFER_SIZE = config.BUFFER_SIZE;
        this.START_POSITION_LAT = config.START_POSITION_LAT;
        this.START_POSITION_LON = config.START_POSITION_LON;
        this.PARSER_TYPE = config.PARSER_TYPE;
        this.FRAME_DELIMITER = config.FRAME_DELIMITER;
        this.FRAME_PATTERN = config.FRAME_PATTERN;
        this.DISCORD_TOKEN = config.DISCORD_TOKEN;
        this.DISCORD_CHANNEL_NAME = config.DISCORD_CHANNEL_NAME;
        this.commandsList = config.commandsList;
        this.sensorRepository = config.sensorRepository;
        this.notificationMessageKeys = config.notificationMessageKeys;
        this.notificationSchedule = config.notificationSchedule;

        if(config.FRAME_PATTERN.values().stream().anyMatch(l -> l.contains(this.sensorRepository.getGpsSensor().getLatitude().getName()))){
            this.sensorRepository.addSensor(this.sensorRepository.getGpsSensor().getLatitude());
        }
        if(config.FRAME_PATTERN.values().stream().anyMatch(l -> l.contains(this.sensorRepository.getGpsSensor().getLongitude().getName()))){
            this.sensorRepository.addSensor(this.sensorRepository.getGpsSensor().getLongitude());
        }
        if(config.FRAME_PATTERN.values().stream().anyMatch(l -> l.contains(this.sensorRepository.getGyroSensor().getAxis_x().getName()))){
            this.sensorRepository.addSensor(this.sensorRepository.getGyroSensor().getAxis_x());
        }
        if(config.FRAME_PATTERN.values().stream().anyMatch(l -> l.contains(this.sensorRepository.getGyroSensor().getAxis_y().getName()))){
            this.sensorRepository.addSensor(this.sensorRepository.getGyroSensor().getAxis_y());
        }
        if(config.FRAME_PATTERN.values().stream().anyMatch(l -> l.contains(this.sensorRepository.getGyroSensor().getAxis_z().getName()))){
            this.sensorRepository.addSensor(this.sensorRepository.getGyroSensor().getAxis_z());
        }

        var basicSensors = this.sensorRepository.getAllBasicSensors().values().stream().toArray();

        Arrays.stream(basicSensors).filter(s -> s instanceof FillingLevelSensor).forEach(s -> {
            var sensor = (FillingLevelSensor)s;
            this.sensorRepository.addSensor(sensor.getHallSensor1());
            this.sensorRepository.addSensor(sensor.getHallSensor2());
            this.sensorRepository.addSensor(sensor.getHallSensor3());
            this.sensorRepository.addSensor(sensor.getHallSensor4());
            this.sensorRepository.addSensor(sensor.getHallSensor5());
            sensor.observeFields();
        });

        this.sensorRepository.getGyroSensor().observeFields();
        this.sensorRepository.getGpsSensor().observeFields();

        Arrays.stream(basicSensors).filter(s -> s instanceof ByteSensor).forEach(s -> {
            for (Sensor innerSensor: ((ByteSensor) s).getSensors()) {
                if(!innerSensor.getName().isEmpty())
                    this.sensorRepository.addSensor(innerSensor);
            }
        });

        Arrays.stream(basicSensors).filter(s -> s instanceof PoteznyTanwiarzSensor).forEach(s -> {
            for (Sensor innerSensor: ((PoteznyTanwiarzSensor) s).getSensors()) {
                if(!innerSensor.getName().isEmpty())
                    this.sensorRepository.addSensor(innerSensor);
            }
        });
    }

    public void setupApplicationConfig(List<BasicController> controllersList) {
        this.controllersList = controllersList;
        List<Triplet<BasicController, List<ISensor>, List<ICommand>>> controllersConfig = new ArrayList<>();
        controllersList.forEach(basicController -> controllersConfig.add(new Triplet<>(basicController,new ArrayList<>(),new ArrayList<>())));

        for (int i = 0; i < controllersConfig.size(); i++) {

            if (Configuration.getInstance().sensorRepository.getGpsSensor().getDestinationControllerNames().contains(controllersConfig.get(i).getValue0().getControllerNameEnum())) {
                Configuration.getInstance().sensorRepository.getGpsSensor().addListener(controllersConfig.get(i).getValue0());
            }
            if (Configuration.getInstance().sensorRepository.getGyroSensor().getDestinationControllerNames().contains(controllersConfig.get(i).getValue0().getControllerNameEnum())) {
                Configuration.getInstance().sensorRepository.getGyroSensor().addListener(controllersConfig.get(i).getValue0());
            }
            int finalI = i;
            Configuration.getInstance().sensorRepository.getAllBasicSensors().keySet().forEach(s -> {
                if (Configuration.getInstance().sensorRepository.getAllBasicSensors().get(s).getDestinationControllerNames().contains(controllersConfig.get(finalI).getValue0().getControllerNameEnum())) {
                    Configuration.getInstance().sensorRepository.getAllBasicSensors().get(s).addListener(controllersConfig.get(finalI).getValue0());
                    controllersConfig.get(finalI).getValue1().add(Configuration.getInstance().sensorRepository.getAllBasicSensors().get(s));
                }
            });

            for (int j = 0; j < Configuration.getInstance().commandsList.size(); j++) {
                if (Configuration.getInstance().commandsList.get(j).getDestinationControllerNames().contains(controllersConfig.get(i).getValue0().getControllerNameEnum())) {
                    controllersConfig.get(i).getValue2().add(Configuration.getInstance().commandsList.get(j));
                }
            }
        }

        for (Triplet<BasicController, List<ISensor>, List<ICommand>> objects : controllersConfig) {
            if (objects.getValue0() instanceof BasicButtonSensorController) {
                ((BasicButtonSensorController) objects.getValue0()).injectSensorsModels(objects.getValue1());
                ((BasicButtonSensorController) objects.getValue0()).assignsCommands(objects.getValue2());
            } else if (objects.getValue0() instanceof BasicSensorController) {
                ((BasicSensorController) objects.getValue0()).injectSensorsModels(objects.getValue1());
            } else if (objects.getValue0() instanceof BasicButtonController) {
                ((BasicButtonController) objects.getValue0()).assignsCommands(objects.getValue2());
            }
        }
    }

    public static Configuration getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final Configuration INSTANCE = new Configuration();
    }
}

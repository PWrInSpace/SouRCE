package pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration;

import org.javatuples.Triplet;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.Schedule;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.SensorRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Configuration {

    public int FPS = 10;

    public double START_POSITION_LAT = 49.013517;

    public double START_POSITION_LON = 8.404435;

    public static final String CONFIG_PATH = "./";

    public static final String CONFIG_FILE_NAME = "config.json";

    public static final String FLIGHT_DATA_PATH = "./flightData/";

    public static final String FLIGHT_DATA_FILE_NAME = "Flight_" + Instant.now().getEpochSecond() + ".txt";

    public String DISCORD_TOKEN = "NzY3MDE4NjExMjM1NjE4ODM5.X4rzvw.Lt1BwdXcZhgoWyl8WC2P1VCSwos";

    public String DISCORD_CHANNEL_NAME = "";

    public String FRAME_DELIMITER = ",";

    public List<String> FRAME_PATTERN = new ArrayList<>();

    public List<Command> commandsList = new LinkedList<>();

    public List<Schedule> notificationSchedule = new LinkedList<>();

    public List<String> notificationMessageKeys = new LinkedList<>();

    public SensorRepository sensorRepository = new SensorRepository();

    private Configuration() {
        if (Holder.INSTANCE != null) {
            throw new IllegalStateException("Singleton already constructed");
        }
    }

    public void setupConfig(ConfigurationSaveModel config) {
        this.FPS = config.FPS;
        this.START_POSITION_LAT = config.START_POSITION_LAT;
        this.START_POSITION_LON = config.START_POSITION_LON;
        this.FRAME_DELIMITER = config.FRAME_DELIMITER;
        this.FRAME_PATTERN = config.FRAME_PATTERN;
        this.DISCORD_TOKEN = config.DISCORD_TOKEN;
        this.DISCORD_CHANNEL_NAME = config.DISCORD_CHANNEL_NAME;
        this.commandsList = config.commandsList;
        this.sensorRepository = config.sensorRepository;
        this.notificationMessageKeys = config.notificationMessageKeys;
        this.notificationSchedule = config.notificationSchedule;
        this.sensorRepository.addSensor(this.sensorRepository.getGpsSensor().getLatitude());
        this.sensorRepository.addSensor(this.sensorRepository.getGpsSensor().getLongitude());
        this.sensorRepository.addSensor(this.sensorRepository.getGyroSensor().getAxis_x());
        this.sensorRepository.addSensor(this.sensorRepository.getGyroSensor().getAxis_y());
        this.sensorRepository.addSensor(this.sensorRepository.getGyroSensor().getAxis_z());
        this.sensorRepository.getGyroSensor().observeFields();
        this.sensorRepository.getGpsSensor().observeFields();
    }

    public static void setupApplicationConfig(List<Triplet<BasicController, List<ISensor>, List<ICommand>>> controllersConfig) {
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

package pl.edu.pwr.pwrinspace.poliwrocket;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.*;
import java.util.LinkedList;
import java.util.List;

public class Configuration {

    public int FPS = 10;

    public double START_POSITION_LAT = 49.013517;

    public double START_POSITION_LON = 8.404435;

    public static final String CONFIG_PATH = "./";

    public static final String CONFIG_FILE_NAME = "config.json";

    public List<Command> commandsListValves = new LinkedList<>();

    public SensorRepository sensorRepository = new SensorRepository();

    private Configuration() {
        if (Holder.INSTANCE != null) {
            throw new IllegalStateException("Singleton already constructed");
        }
    }

    public void setupConfig(ConfigurationSaveModel config){
        this.FPS = config.FPS;
        this.START_POSITION_LAT = config.START_POSITION_LAT;
        this.START_POSITION_LON = config.START_POSITION_LON;
        this.commandsListValves = config.commandsListValves;
        this.sensorRepository = config.sensorRepository;
        this.sensorRepository.addSensor(this.sensorRepository.getGpsSensor().getLatitude());
        this.sensorRepository.addSensor(this.sensorRepository.getGpsSensor().getLongitude());
        this.sensorRepository.addSensor(this.sensorRepository.getGyroSensor().getAxis_x());
        this.sensorRepository.addSensor(this.sensorRepository.getGyroSensor().getAxis_y());
        this.sensorRepository.addSensor(this.sensorRepository.getGyroSensor().getAxis_z());
        this.sensorRepository.getGyroSensor().observeFields();
        this.sensorRepository.getGpsSensor().observeFields();
    }

    public static Configuration getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final Configuration INSTANCE = new Configuration();
    }
}

package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SensorRepository implements ISensorRepository {

    private static final Logger logger = LoggerFactory.getLogger(SensorRepository.class);

    @Expose
    private HashMap<String, Sensor> sensors = new HashMap<>();

    @Expose
    private GPSSensor gpsSensor = new GPSSensor();

    @Expose
    private GyroSensor gyroSensor = new GyroSensor();

    @Override
    public Sensor getSensorByName(String name) {
        try {
            return sensors.get(name);
        } catch (NullPointerException e) {
            logger.error("Sensor not found in repository: {}",name);
            throw e;
        }
    }

    @Override
    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getName(),sensor);
    }

    @Override
    public void removeSensor(ISensor sensor) {
        sensors.remove(sensor.getName());
    }

    @Override
    public void updateSensor(ISensor sensor) {
      sensors.get(sensor.getName()).setValue(sensor.getValue());
    }

    @Override
    public Set<String> getSensorsKeys() {
        return sensors.keySet();
    }

    public GPSSensor getGpsSensor() {
        return gpsSensor;
    }

    public GyroSensor getGyroSensor() {
        return gyroSensor;
    }

    public GPSSensor setGpsSensor(GPSSensor gpsSensor) {
        this.gpsSensor = gpsSensor;
        return this.gpsSensor;
    }

    public GyroSensor setGyroSensor(GyroSensor gyroSensor) {
        this.gyroSensor = gyroSensor;
        return this.gyroSensor;
    }

    public Map<String, Sensor> getAllBasicSensors() {
        return this.sensors;
    }

    public String getSensorKeysAndValues(){
        StringBuilder res = new StringBuilder();
        sensors.keySet().stream().sorted().forEach(key -> res.append(key).append('=').append(sensors.get(key).getValue()).append("\n"));
        return res.toString();
    }
}

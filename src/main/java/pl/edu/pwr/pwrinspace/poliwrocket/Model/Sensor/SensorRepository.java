package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class SensorRepository implements ISensorRepository {

    private static final Logger logger = LoggerFactory.getLogger(SensorRepository.class);

    @JsonProperty("sensors")
    private LinkedHashMap<String, Sensor> sensors = new LinkedHashMap<>();

    @JsonProperty("gpsSensor")
    private GPSSensor gpsSensor = new GPSSensor();

    @JsonProperty("gyroSensor")
    private GyroSensor gyroSensor = new GyroSensor();

    @Override
    public Sensor getSensorByName(String name) throws NullPointerException {
        var sensor = sensors.get(name);
        if (sensor == null) {
            logger.error("Sensor not found in repository: {}", name);
            throw new NullPointerException();
        }
        return sensor;
    }

    @Override
    public void addSensor(Sensor sensor) throws RuntimeException {
        if (sensors.get(sensor.getName()) != null) {
            var message = "Sensor with name " + sensor.getName() + " already exist.";
            logger.error(message);
            throw new RuntimeException(message);

        }
        sensors.put(sensor.getName(), sensor);
    }

    @Override
    public void addSensor(String key, Sensor sensor) throws RuntimeException {
        if (sensors.get(key) != null) {
            var message = "Sensor with key " + key + " already exist.";
            logger.error(message);
            throw new RuntimeException(message);
        }
        sensors.put(key,sensor);
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

    public void setGpsSensor(GPSSensor gpsSensor) {
        this.gpsSensor = gpsSensor;
    }

    public void setGyroSensor(GyroSensor gyroSensor) {
        this.gyroSensor = gyroSensor;
    }

    public Map<String, Sensor> getAllBasicSensors() {
        return this.sensors;
    }

    public String getSensorKeysAndValues(){
        StringBuilder res = new StringBuilder();
        sensors.keySet().stream().sorted().forEach(key -> {
            var sensor = sensors.get(key);
            if(!sensor.isHidden()) {
                res.append(key).append('=').append(sensor.getValue());

                if(sensor.hasInterpreter())
                    res.append(" - ").append(sensor.getCodeMeaning().text);

                res.append("\n");
            }
        });
        return res.toString();
    }
}

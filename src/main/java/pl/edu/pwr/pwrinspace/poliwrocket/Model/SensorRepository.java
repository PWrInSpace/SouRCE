package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import java.util.HashMap;
import java.util.Set;

public class SensorRepository implements ISensorRepository {
    private HashMap<String,ISensor> sensors = new HashMap<>();

    @Override
    public ISensor getSensorByName(String name) {
        return sensors.get(name);
    }

    @Override
    public void addSensor(Sensor sensor) {
        sensors.put(sensor.getName(),sensor);
    }

    @Override
    public void removeSensor(Sensor sensor) {
        sensors.remove(sensor.getName());
    }

    @Override
    public void updateSensor(Sensor sensor) {
      sensors.get(sensor.getName()).setValue(sensor.getValue());
    }

    @Override
    public Set<String> getSensorsKeys() {
        return sensors.keySet();
    }

}

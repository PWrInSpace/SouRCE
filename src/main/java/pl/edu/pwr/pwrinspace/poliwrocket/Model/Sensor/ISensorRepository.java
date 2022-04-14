package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import java.util.Map;
import java.util.Set;

public interface ISensorRepository {
    Sensor getSensorByName(String name);
    void addSensor(Sensor sensor);
    void removeSensor(ISensor sensor);
    void updateSensor(ISensor sensor);
    Set<String> getSensorsKeys();
    Map<String, Sensor> getAllBasicSensors();
}

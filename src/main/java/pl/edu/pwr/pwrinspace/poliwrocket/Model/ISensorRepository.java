package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import java.util.Set;

public interface ISensorRepository {
    ISensor getSensorByName(String name);
    void addSensor(Sensor sensor);
    void removeSensor(Sensor sensor);
    void updateSensor(Sensor sensor);
    Set<String> getSensorsKeys();
}

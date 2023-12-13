package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

public interface IGyroSensor extends IFieldsObserver {
     double getX();
     double getY();
     double getZ();
     void assignSensors(Sensor axis_x, Sensor axis_y, Sensor axis_z);
}

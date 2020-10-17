package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

public interface ISensorUI extends ISensor {
    String getUnit();
    double getMaxRange();
    double getMinRange();
}

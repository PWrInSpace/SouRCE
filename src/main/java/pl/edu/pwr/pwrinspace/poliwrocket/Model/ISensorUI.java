package pl.edu.pwr.pwrinspace.poliwrocket.Model;

public interface ISensorUI extends ISensor {
    String getName();
    String getUnit();
    double getMaxRange();
    double getMinRange();
}

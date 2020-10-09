package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import java.time.Instant;

public interface ISensor {

    double getValue();
    String getDestination();
    void setValue(double newValue);
    void setDestination(String destination);
    Instant getTimeStamp();
}

package pl.edu.pwr.pwrinspace.poliwrocket.Model;

public interface ISensor {

    double getValue();
    String getDestination();
    void setValue(double newValue);
    void setDestination(String destination);
}

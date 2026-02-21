package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;

public class SensorDestination {
    @Expose
    private String destination;
    @Expose
    private String destinationControllerName;

    public SensorDestination(String destination, String destinationControllerName) {
        this.destination = destination;
        this.destinationControllerName = destinationControllerName;
    }

    public String getDestination() {
        return destination;
    }

    public String getDestinationControllerName() {
        return destinationControllerName;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }
}

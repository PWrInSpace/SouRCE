package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SensorDestination {
    @JsonProperty("destination")
    private String destination;
    @JsonProperty("destinationControllerName")
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

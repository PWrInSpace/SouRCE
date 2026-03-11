package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("!TareSensor")
public class TareSensor extends Sensor implements ITare {

    @JsonProperty("tareValue")
    private double tareValue = 0;

    @Override
    public double getValue() {
        return super.getValue() - tareValue;
    }

    @Override
    public double getTareValue() {
        return tareValue;
    }

    @Override
    public void setTareValue(double newValue) {
        tareValue = newValue;
    }
}

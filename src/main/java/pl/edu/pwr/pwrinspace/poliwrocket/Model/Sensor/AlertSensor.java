package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("AlertSensor")
public class AlertSensor extends Sensor implements IAlert {

    @JsonProperty("alertDelta")
    private double alertDelta = 0;

    @JsonProperty("nominalValue")
    private double nominalValue = 0;

    private boolean alertValue = false;

    @Override
    public boolean getAlert() {
        return alertValue;
    }

    @Override
    public void setValue(double newValue) {
        alertValue = Math.abs(nominalValue - newValue) > alertDelta;
        super.setValue(newValue);
    }
}

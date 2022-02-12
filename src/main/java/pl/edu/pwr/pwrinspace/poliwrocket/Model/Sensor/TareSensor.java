package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;

public class TareSensor extends Sensor implements ITare {

    @Expose
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

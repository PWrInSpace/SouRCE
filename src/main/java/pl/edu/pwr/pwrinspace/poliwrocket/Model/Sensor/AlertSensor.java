package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;

public class AlertSensor extends Sensor implements IAlert {

    @Expose
    private double alertDelta = 0;

    @Expose
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

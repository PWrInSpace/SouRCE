package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;

public class ByteSensor extends Sensor {

    @Expose
    private final Sensor[] sensors = new Sensor[7];

    public ByteSensor() {
        super();
        this.setBoolean(true);
        for (int i = 0; i < sensors.length; i++) {
            sensors[i] = new Sensor();
        }
    }

    public Sensor[] getSensors() {
        return sensors;
    }

    @Override
    protected void notifyObserver() {
        String values = Integer.toBinaryString((int)this.getValue());
        if(values.length() == 7)
            for (int i = 0; i < values.length(); i++) {
                sensors[i].setValue(bitToDouble(values.charAt(i)));
            }
    }

    private double bitToDouble(char value) {
        return value == '1' ? 1.0 : 0.0;
    }
}

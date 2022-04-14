package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;

public class PoteznyTanwiarzSensor extends Sensor implements ISensorsWrapper {

    @Expose
    private final TanwiarzSensor[] sensors = new TanwiarzSensor[5];

    public PoteznyTanwiarzSensor() {
        super();
        for (int i = 0; i < sensors.length; i++) {
            sensors[i] = new TanwiarzSensor();
        }
    }

    @Override
    public void setValue(double newValue) {
        super.setValue(newValue);
        for (int i = 0; i < sensors.length; i++) {
            sensors[i].setValue(newValue);
        }
    }

    public Sensor[] getSensors() {
        return sensors;
    }
}

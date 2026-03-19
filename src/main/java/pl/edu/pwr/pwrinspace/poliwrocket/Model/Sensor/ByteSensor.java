package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;

@JsonTypeName("ByteSensor")
public class ByteSensor extends Sensor implements ISensorsWrapper {

    @JsonProperty("numberOfBytes")
    private int numberOfBytes = 1;
    @JsonProperty("sensors")
    private Sensor[] sensors = new Sensor[this.numberOfBits()];

    public int numberOfBits() {
        return numberOfBytes * 8;
    }

    public Sensor[] getSensors() {
        return sensors;
    }

    @Override
    protected void notifyObserver() {
        int valueInt = (int) this.getValue();

        for (int i = 0; i < numberOfBits(); i++) {
            int bit = (valueInt >> i) & 1;
            sensors[i].setValue(bit);
        }
    }

    private double bitToDouble (char value) {
        return value == '1' ? 1.0 : 0.0;
    }
}

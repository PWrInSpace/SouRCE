package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;

public class ByteSensor extends Sensor implements ISensorsWrapper {

    @Expose
    private final Sensor[] sensors = new Sensor[8];

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

   /* @Override
    protected void notifyObserver() {
        int valasInt= (int)this.getValue();
        String values = String.format("%" + 8 + "s", Integer.toBinaryString(valasInt)).replaceAll(" ", "0");
        int k = 0;
        if(values.length() == 8)
            for (int i = values.length() - 1; i >= 0; i--) {
                sensors[k].setValue(bitToDouble(values.charAt(i)));
                k++;
            }
    }*/

    @Override
    protected void notifyObserver() {
        int valueInt = (int)this.getValue();
        String values = String.format("%8s", Integer.toBinaryString(valueInt)).replace(' ', '0');
        int k = 7;
        if(values.length() == 8)
            for (int i = 0; i < values.length(); i++) {
                sensors[i].setValue(bitToDouble(values.charAt(k)));
                k--;
            }
    }

    private double bitToDouble(char value) {
        return value == '1' ? 1.0 : 0.0;
    }
}

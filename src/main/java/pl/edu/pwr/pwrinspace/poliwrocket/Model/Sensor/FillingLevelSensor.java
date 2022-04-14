package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;
import javafx.beans.Observable;

public class FillingLevelSensor extends Sensor implements IFieldsObserver {

    @Expose
    private AlertSensor hallSensor1;
    private boolean isSensor1Updated = false;

    @Expose
    private AlertSensor hallSensor2;
    private boolean isSensor2Updated = false;

    @Expose
    private AlertSensor hallSensor3;
    private boolean isSensor3Updated = false;

    @Expose
    private AlertSensor hallSensor4;
    private boolean isSensor4Updated = false;

    public AlertSensor getHallSensor1() {
        return hallSensor1;
    }

    public void setHallSensor1(AlertSensor hallSensor1) {
        this.hallSensor1 = hallSensor1;
    }

    public AlertSensor getHallSensor2() {
        return hallSensor2;
    }

    public void setHallSensor2(AlertSensor hallSensor2) {
        this.hallSensor2 = hallSensor2;
    }

    public AlertSensor getHallSensor3() {
        return hallSensor3;
    }

    public void setHallSensor3(AlertSensor hallSensor3) {
        this.hallSensor3 = hallSensor3;
    }

    public AlertSensor getHallSensor4() {
        return hallSensor4;
    }

    public void setHallSensor4(AlertSensor hallSensor4) {
        this.hallSensor4 = hallSensor4;
    }

    public AlertSensor getHallSensor5() {
        return hallSensor5;
    }

    public void setHallSensor5(AlertSensor hallSensor5) {
        this.hallSensor5 = hallSensor5;
    }

    @Expose
    private AlertSensor hallSensor5;
    private boolean isSensor5Updated = false;

    public FillingLevelSensor() {
        super();
    }

    public void observeFields(){
        this.hallSensor1.addListener(this);
        this.hallSensor2.addListener(this);
        this.hallSensor3.addListener(this);
        this.hallSensor4.addListener(this);
        this.hallSensor5.addListener(this);
    }


    @Override
    public void invalidated(Observable observable) {
        if (hallSensor1.equals(observable)) {
            isSensor1Updated = true;
        } else if (hallSensor2.equals(observable)) {
            isSensor2Updated = true;

        } else if (hallSensor3.equals(observable)) {
            isSensor3Updated = true;

        } else if (hallSensor4.equals(observable)) {
            isSensor4Updated = true;

        } else if (hallSensor5.equals(observable)) {
            isSensor5Updated = true;
        }

        if(isSensor1Updated && isSensor2Updated && isSensor3Updated && isSensor4Updated && isSensor5Updated) {
            double currentValue = estimateCurrentLevel();
            if (getPreviousValue() != currentValue)
                setValue(currentValue);

        }
    }
    private double estimateCurrentLevel() {

        if((getValue() == 0 || getValue() == 40 || getValue() == 100) && hallSensor1.getAlert()) {
            return 20;
        }
        if((getValue() == 20 || getValue() == 60) && hallSensor2.getAlert()) {
            return 40;
        }
        if((getValue() == 20 || getValue() == 40 || getValue() == 80) && hallSensor3.getAlert()) {
            return 60;
        }
        if((getValue() == 40 ||  getValue() == 60 || getValue() == 100) && hallSensor4.getAlert()) {
            return 80;
        }
        if((getValue() == 60 || getValue() == 80 || getValue() == 100) && hallSensor5.getAlert()) {
            return 100;
        }

        return getValue();
    }
}

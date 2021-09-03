package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

public class FillingLevelSensor extends Sensor implements InvalidationListener {

    @Expose
    private Sensor hallSensor1;
    private boolean isSensor1Updated = false;

    @Expose
    private Sensor hallSensor2;
    private boolean isSensor2Updated = false;

    @Expose
    private Sensor hallSensor3;
    private boolean isSensor3Updated = false;

    @Expose
    private Sensor hallSensor4;
    private boolean isSensor4Updated = false;

    public Sensor getHallSensor1() {
        return hallSensor1;
    }

    public void setHallSensor1(Sensor hallSensor1) {
        this.hallSensor1 = hallSensor1;
    }

    public Sensor getHallSensor2() {
        return hallSensor2;
    }

    public void setHallSensor2(Sensor hallSensor2) {
        this.hallSensor2 = hallSensor2;
    }

    public Sensor getHallSensor3() {
        return hallSensor3;
    }

    public void setHallSensor3(Sensor hallSensor3) {
        this.hallSensor3 = hallSensor3;
    }

    public Sensor getHallSensor4() {
        return hallSensor4;
    }

    public void setHallSensor4(Sensor hallSensor4) {
        this.hallSensor4 = hallSensor4;
    }

    public Sensor getHallSensor5() {
        return hallSensor5;
    }

    public void setHallSensor5(Sensor hallSensor5) {
        this.hallSensor5 = hallSensor5;
    }

    @Expose
    private Sensor hallSensor5;
    private boolean isSensor5Updated = false;

    public FillingLevelSensor() {
        super();
        hallSensor1 = new Sensor();
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
            this.setValue(currentValue);

        }
    }

        private double estimateCurrentLevel() {
            //TODO
            return 0;
        }
    }

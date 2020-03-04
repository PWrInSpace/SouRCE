package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.List;

public class GyroSensor implements Observable, InvalidationListener {

    private ISensor axis_x;
    private ISensor axis_y;
    private ISensor axis_z;
    List<InvalidationListener> observators = new ArrayList<>();

    public GyroSensor(GyroSensor sensor) {
        this.axis_x= sensor.axis_x;
        this.axis_y= sensor.axis_y;
        this.axis_z= sensor.axis_z;
    }
    public GyroSensor(Sensor axis_x, Sensor axis_y, Sensor axis_z){
        this.axis_x=axis_x;
        this.axis_y=axis_y;
        this.axis_z=axis_z;
    }


    public double[] getValueGyro() {
        return new double[]{axis_x.getValue(), axis_y.getValue(), axis_z.getValue()};
    }

    private void notifyObserver(){
        for (InvalidationListener obs:  observators) {
//            obs.invalidated(new GyroSensor(this));
            obs.invalidated(this);
        }
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observators.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        observators.remove(invalidationListener);
    }

    @Override
    public void invalidated(Observable observable) {
        notifyObserver();
    }

}

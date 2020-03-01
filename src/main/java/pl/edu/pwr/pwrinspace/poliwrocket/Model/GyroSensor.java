package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.List;

public class GyroSensor extends Sensor implements Observable {

    private double axis_x = 0;
    private double axis_y = 0;
    private double axis_z = 0;

    List<InvalidationListener> observators = new ArrayList<>();

    public GyroSensor(GyroSensor sensor) {

        this.axis_x= sensor.axis_x;
        this.axis_y= sensor.axis_y;
        this.axis_z= sensor.axis_z;
    }
    public GyroSensor(){

    }


    public double[] getValueGyro() {
        return new double[]{axis_x, axis_y, axis_z};
    }

    public void update(double axis_x, double axis_y, double axis_z){
        this.axis_x= axis_x;
        this.axis_y= axis_y;
        this.axis_z= axis_z;
        notifyObserver();
    }

    private void notifyObserver(){
        for (InvalidationListener obs:  observators) {
            obs.invalidated(new GyroSensor(this));
        }
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observators.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {

    }
}

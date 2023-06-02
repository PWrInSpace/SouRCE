package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.google.gson.annotations.Expose;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.ArrayList;
import java.util.List;

public class GyroSensor implements Observable, IGyroSensor {

    @Expose
    private Sensor axis_x;

    @Expose
    private Sensor axis_y;

    @Expose
    private Sensor axis_z;

    @Expose
    private List<String> destinationControllerNames = new ArrayList<>();

    List<InvalidationListener> observers = new ArrayList<>();

    public GyroSensor(){

    }

    public Sensor getAxis_x() {
        return axis_x;
    }

    public Sensor getAxis_y() {
        return axis_y;
    }

    public Sensor getAxis_z() {
        return axis_z;
    }

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

    public void observeFields(){
        this.axis_x.addListener(this);
        this.axis_y.addListener(this);
        this.axis_z.addListener(this);
    }

    @Override
    public double getX() {
        return axis_x.getValue();
    }

    @Override
    public double getY() {
        return axis_y.getValue();
    }

    @Override
    public double getZ() {
        return axis_z.getValue();
    }

    @Override
    public void assignSensors(Sensor axis_x, Sensor axis_y, Sensor axis_z) {
        this.axis_x = axis_x;
        this.axis_y = axis_y;
        this.axis_z = axis_z;
    }

    private void notifyObserver(){
        for (InvalidationListener obs: observers) {
            obs.invalidated(this);
        }
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        observers.remove(invalidationListener);
    }

    @Override
    public void invalidated(Observable observable) {
        notifyObserver();
    }

    public List<String> getDestinationControllerNames() {
        return destinationControllerNames;
    }

    public void setDestinationControllerNames(List<String> destinationControllerNames) {
        this.destinationControllerNames = destinationControllerNames;
    }
}

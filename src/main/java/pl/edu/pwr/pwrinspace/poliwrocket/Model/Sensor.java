package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Sensor implements Observable, ISensor {

    private List<InvalidationListener> observators = new ArrayList<>();
    private String destination;
    private String name = "Altitude";
    private String unit = "m";
    private Instant timeStamp;
    private double maxRange = 360;
    private double minRange = -360;
    private double value = 0;

    public Sensor() {
    }

    public Sensor(@NotNull Sensor sensor) {
        this.destination = sensor.destination;
        this.unit = sensor.unit;
        this.name = sensor.name;
        this.minRange = sensor.minRange;
        this.maxRange = sensor.maxRange;
        this.value = sensor.value;
        this.timeStamp = sensor.timeStamp;
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
    public void setDestination(String destination){
        this.destination = destination;
    }

    @Override
    public String getDestination(){
        return destination;
    }

    public Instant getTimeStamp() {
        return timeStamp;
    }

    private void notifyObserver(){
        for (InvalidationListener obs: observators) {
            //obs.invalidated(new Sensor(this));
            obs.invalidated(this);
        }
    }

    @Override
    public void setValue(double newValue){
        this.value = newValue;
        this.timeStamp = Instant.now();
        notifyObserver();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getUnit() {
        return unit;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public double getMinRange() {
        return minRange;
    }

    @Override
    public double getValue(){
        return value;
    }

    //TODO SPRAWDZIC TO
    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

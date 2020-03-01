package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GPSSensor implements Observable, IGPSSensor {

    List<InvalidationListener> observators = new ArrayList<>();

    private double latitude;

    private double longitude;

    private static int dataNumber = 0;

    public GPSSensor() {
    }

    public GPSSensor(@NotNull GPSSensor gpsSensor) {
        this.longitude = gpsSensor.longitude;
        this.latitude = gpsSensor.latitude;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observators.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        observators.remove(invalidationListener);
    }

    public void setPosition(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
        notifyObserver();
    }

    private void notifyObserver(){
        for (InvalidationListener obs:  observators) {
            obs.invalidated(new GPSSensor(this));
        }
    }

    public double[] getPosition(){
        return new double [] {latitude,longitude};
    }

    @Override
    public int getDataNumber() {
        return dataNumber;
    }

}

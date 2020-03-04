package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import java.util.ArrayList;
import java.util.List;

public class GPSSensor implements Observable, IGPSSensor, InvalidationListener {

    List<InvalidationListener> observators = new ArrayList<>();

    private ISensor latitude;

    private ISensor longitude;

    private int isReadyForUpdate = 0; //number 2 means true

    public GPSSensor() {
    }

    public GPSSensor(ISensor latitude, ISensor longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observators.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        observators.remove(invalidationListener);
    }

    private void notifyObserver(){
        for (InvalidationListener obs:  observators) {
//            obs.invalidated(new GPSSensor(latitude,longitude));
            obs.invalidated(this);
        }
    }

    @Override
    public void setPosition(double latitude, double longitude) {
        this.latitude.setValue(latitude);
        this.longitude.setValue(longitude);
        notifyObserver();
    }

    @Override
    public double[] getPosition(){
        return new double [] {latitude.getValue(),longitude.getValue()};
    }

    //TODO SPRAWDZIC TO
    @Override
    public void invalidated(Observable observable) {
        if(observable == longitude){
            isReadyForUpdate = 1;
        }
        if(observable == latitude && isReadyForUpdate == 1){
            isReadyForUpdate = 2;
        }
        if(isReadyForUpdate == 2){
            notifyObserver();
            isReadyForUpdate = 0;
        }
    }
}

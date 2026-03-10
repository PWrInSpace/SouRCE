package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import pl.edu.pwr.pwrinspace.poliwrocket.Event.IUIUpdateEventListener;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GPSSensor implements Observable, IGPSSensor, IUIUpdateEventListener {

    List<InvalidationListener> observers = new ArrayList<>();

    @JsonProperty("latitude")
    public Sensor latitude = new Sensor();
    @JsonProperty("longitude")
    public Sensor longitude = new Sensor();
    @JsonProperty("sensorDestinations")
    private List<SensorDestination> sensorDestinations = new ArrayList<>();

    private boolean isLatUpToDate = false;

    private boolean isLongUpToDate = false;

    public GPSSensor() {
    }

    public GPSSensor(Sensor latitude, Sensor longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public void observeFields(){
        this.latitude.addListener(this);
        this.longitude.addListener(this);
    }

    public Sensor getLatitude() {
        return latitude;
    }

    public Sensor getLongitude() {
        return longitude;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        observers.remove(invalidationListener);
    }

    private void notifyObserver() {
        for (InvalidationListener obs : observers) {
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
    public Map<String, Double> getPosition() {
        HashMap<String, Double> position = new HashMap<>();
        position.put(IGPSSensor.LATITUDE_KEY,latitude.getValue());
        position.put(IGPSSensor.LONGITUDE_KEY,longitude.getValue());
        return position;
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable == longitude) {
            isLongUpToDate = true;
        }
        if (observable == latitude) {
            isLatUpToDate = true;
        }
        if (isLongUpToDate && isLatUpToDate
                && Math.abs(latitude.getValue() - Configuration.getInstance().START_POSITION_LAT) < 2.0
                ) {
            notifyObserver();
            isLongUpToDate = false;
            isLatUpToDate = false;
        }
    }

    public List<SensorDestination> getSenorDestinations() {
        return sensorDestinations;
    }

    public void setSensorDestinations(List<SensorDestination> sensorDestinations) {
        this.sensorDestinations = sensorDestinations;
    }

    @Override
    public void onUIUpdateEvent() {
        notifyObserver();
    }

    public boolean containsControllerName(String controllerName) {
        for (SensorDestination sensorDestination : sensorDestinations) {
            if (sensorDestination.getDestinationControllerName().equals(controllerName)) {
                return true;
            }
        }
        return false;
    }
}

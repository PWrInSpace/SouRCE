package pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import javafx.beans.InvalidationListener;

public interface IFieldsObserver extends InvalidationListener {
    void observeFields();
}

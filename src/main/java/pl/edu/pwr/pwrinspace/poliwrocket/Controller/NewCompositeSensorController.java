package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.CompositeBitSensor;

public class NewCompositeSensorController extends AddExistingSensorController {
    @FXML
    protected JFXListView<String> availableByteSensorListView;
    @FXML
    protected JFXListView<String> selectedByteSensorListView;

    Configuration config = Configuration.getInstance();

    @Override
    @FXML
    public void initialize() {
        super.initialize();

        sensorTypeFilter.getItems().clear();
        sensorTypeFilter.getItems().add("CompositeBitSensor");
        sensorTypeFilter.getSelectionModel().selectFirst();

        sensorListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleSensorSelection(newValue));
    }

    private void handleSensorSelection(String sensor_) {
        sensorRepository = config.sensorRepository;
        CompositeBitSensor sensor = (CompositeBitSensor) sensorRepository.getSensorByName(sensor_);

        availableByteSensorListView.getItems().clear();
        selectedByteSensorListView.getItems().clear();

        sensorRepository.getAllBasicSensors().forEach((key, s) -> {
            if (sensor.containsKey(s.getName())) selectedByteSensorListView.getItems().add(s.getName());
            else availableByteSensorListView.getItems().add(s.getName());
        });
    }
}

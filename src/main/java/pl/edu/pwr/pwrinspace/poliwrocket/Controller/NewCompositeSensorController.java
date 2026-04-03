package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import javafx.fxml.FXML;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.CompositeBitSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsYamlService;

import java.util.List;

public class NewCompositeSensorController extends AddExistingSensorController {
    @FXML
    protected JFXListView<String> availableByteSensorListView;
    @FXML
    protected JFXListView<String> selectedByteSensorListView;
    @FXML
    protected JFXButton moveRightButton;
    @FXML
    protected JFXButton moveLeftButton;
    @FXML
    protected JFXButton moveUpButton;
    @FXML
    protected JFXButton moveDownButton;
    @FXML
    protected JFXButton saveEditButton;

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

    @FXML
    public void moveRight() {
        String selectedSensor = availableByteSensorListView.getSelectionModel().getSelectedItem();
        if (selectedSensor != null) {
            availableByteSensorListView.getItems().remove(selectedSensor);
            selectedByteSensorListView.getItems().add(selectedSensor);
        }
    }

    @FXML
    public void moveLeft() {
        String selectedSensor = selectedByteSensorListView.getSelectionModel().getSelectedItem();
        if (selectedSensor != null) {
            selectedByteSensorListView.getItems().remove(selectedSensor);
            availableByteSensorListView.getItems().add(selectedSensor);
        }
    }

    @FXML
    public void moveUp() {
        String selectedSensor = selectedByteSensorListView.getSelectionModel().getSelectedItem();
        if (selectedSensor != null) {
            int index = selectedByteSensorListView.getItems().indexOf(selectedSensor);
            if (index > 0) {
                selectedByteSensorListView.getItems().remove(selectedSensor);
                selectedByteSensorListView.getItems().add(index - 1, selectedSensor);
                selectedByteSensorListView.getSelectionModel().select(index - 1);
            }
        }
    }

    @FXML
    public void moveDown() {
        String selectedSensor = selectedByteSensorListView.getSelectionModel().getSelectedItem();
        if (selectedSensor != null) {
            int index = selectedByteSensorListView.getItems().indexOf(selectedSensor);
            if (index < selectedByteSensorListView.getItems().size() - 1) {
                selectedByteSensorListView.getItems().remove(selectedSensor);
                selectedByteSensorListView.getItems().add(index + 1, selectedSensor);
                selectedByteSensorListView.getSelectionModel().select(index + 1);
            }
        }
    }

    @FXML
    public void saveEdit() {
        String sensor_ = sensorListView.getSelectionModel().getSelectedItem();
        if (sensor_ == null) return;

        sensorRepository = config.sensorRepository;
        CompositeBitSensor sensor = (CompositeBitSensor) sensorRepository.getSensorByName(sensor_);

        List<String> selectedSensors = selectedByteSensorListView.getItems();
        String[] sensorsKeys = selectedSensors.toArray(String[]::new);
        sensor.setSensorsKeys(sensorsKeys);

        ModelAsYamlService modelAsYamlService = new ModelAsYamlService();
        try {
            modelAsYamlService.saveToFile(ConfigurationSaveModel.getConfigurationSaveModel(config), true);
            config.reloadConfigInstance(modelAsYamlService.readFromFile(new ConfigurationSaveModel(), true));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}

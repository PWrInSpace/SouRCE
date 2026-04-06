package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.*;
import javafx.fxml.FXML;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ByteSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.CompositeBitSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsYamlService;
import java.security.InvalidParameterException;
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
    @FXML
    protected JFXTextField byteSensorNameFilter;
    @FXML
    protected JFXTextField compositeSensorNameField;
    @FXML
    protected JFXCheckBox newCompositeSensorButton;

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
        if (sensor_ == null) return;
        newCompositeSensorButton.setSelected(false);
        sensorRepository = config.sensorRepository;
        CompositeBitSensor sensor = (CompositeBitSensor) sensorRepository.getSensorByName(sensor_);

        availableByteSensorListView.getItems().clear();
        selectedByteSensorListView.getItems().clear();

        sensorRepository.getAllBasicSensors().forEach((key, s) -> {
            if (s instanceof ByteSensor) {
                for (Sensor bitSensor : ((ByteSensor) s).getSensors()) {
                    if (bitSensor.getName().isEmpty()) continue;
                    if (sensor.containsKey(bitSensor.getName())) selectedByteSensorListView.getItems().add(bitSensor.getName());
                    else availableByteSensorListView.getItems().add(bitSensor.getName());
                }
            }
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
    public void saveEdit() throws InvalidParameterException {
        try {
            CompositeBitSensor sensor;
            if (newCompositeSensorButton.isSelected()) {
                String sensorName = compositeSensorNameField.getText();
                if (sensorName.isEmpty()) {
                    throw new InvalidParameterException("Composite sensor name cannot be empty");
                }
                try {
                    sensorRepository.getSensorByName(sensorName);
                    throw new InvalidParameterException("Sensor with name " + sensorName + " already exist");
                } catch (NullPointerException ignored) {}
                sensor = new CompositeBitSensor();
                sensor.setName(sensorName);
                sensorRepository.addSensor(sensor);
            } else sensor = (CompositeBitSensor) getSelectedSensor();

            List<String> selectedSensors = selectedByteSensorListView.getItems();
            String[] sensorsKeys = selectedSensors.toArray(String[]::new);
            sensor.setSensorsKeys(sensorsKeys);

            ModelAsYamlService modelAsYamlService = new ModelAsYamlService();
            modelAsYamlService.saveToFile(ConfigurationSaveModel.getConfigurationSaveModel(config), true);
            config.reloadConfigInstance(modelAsYamlService.readFromFile(new ConfigurationSaveModel(), true));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    @FXML
    public void createEmptySensor() {
        if (newCompositeSensorButton.isSelected()) {
            sensorListView.getSelectionModel().clearSelection();
            compositeSensorNameField.setText("");
            compositeSensorNameField.setVisible(true);
            selectedByteSensorListView.getItems().clear();
            availableByteSensorListView.getItems().clear();
            sensorRepository.getAllBasicSensors().forEach((key, s) -> {
                if (s instanceof ByteSensor) {
                    for (Sensor bitSensor : ((ByteSensor) s).getSensors()) {
                        if (bitSensor.getName().isEmpty()) continue;
                        availableByteSensorListView.getItems().add(bitSensor.getName());
                    }
                }
            });
        } else compositeSensorNameField.setVisible(false);
    }

    @Override
    protected void updateFilters() {
        String name = nameFilter.getText();

        filteredSensorList.setPredicate(sensor -> {
            if (bannedSensorsList.contains(sensor)) return false;
            if (!sensor.contains(name)) return false;
            return "CompositeBitSensor".equals(sensorRepository.getSensorByName(sensor).getClass().getSimpleName());
        });
    }
}

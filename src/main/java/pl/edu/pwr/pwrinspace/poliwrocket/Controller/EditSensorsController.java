package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ByteSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsYamlService;

public class EditSensorsController extends AddExistingSensorController {
    @FXML
    protected JFXButton saveEditButton;
    @FXML
    protected JFXTextField nameTextField;
    @FXML
    protected JFXTextField unitTextField;
    @FXML
    protected JFXTextField maxRangeTextField;
    @FXML
    protected JFXTextField minRangeTextField;
    @FXML
    protected JFXCheckBox isBooleanCheckbox;
    @FXML
    protected JFXComboBox<String> interpreterKeyComboBox;
    @FXML
    protected JFXCheckBox hiddenCheckBox;
    @FXML
    protected JFXComboBox<String> byteSubSensorComboBox;

    @Override
    @FXML
    public void initialize() {
        super.initialize();
        sensorTypeFilter.getItems().add("ByteSensor");
        interpreterKeyComboBox.getItems().add("None");
        Configuration.getInstance().interpreterRepository.getRepositorySet().forEach((key, interpreter) -> {
            interpreterKeyComboBox.getItems().add(key);
        });
        interpreterKeyComboBox.getSelectionModel().selectFirst();
        sensorListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateFields(newValue));
    }

    @Override
    protected void updateFilters() {
        sensorRepository = Configuration.getInstance().sensorRepository;
        String type = sensorTypeFilter.getSelectionModel().getSelectedItem();
        String name = nameFilter.getText();

        filteredSensorList.setPredicate(sensor -> {
            if (parentController != null) {
                for (String controllerName : sensorRepository.getSensorByName(sensor).getDestinationControllerNames()) {
                    if (controllerName.equals(parentController.getControllerName())) return false;
                }
            }
            if (bannedSensorsList.contains(sensor)) return false;
            if (!sensor.contains(name)) return false;
            if (sensorRepository.getSensorByName(sensor).isSubSensor()) return false;
            return type.equals(sensorRepository.getSensorByName(sensor).getClass().getSimpleName());
        });
    }

    protected void updateFields(String sensor) {
        sensorRepository = Configuration.getInstance().sensorRepository;

        if (sensor == null || sensorRepository.getSensorByName(sensor) == null) return;

        if (sensorRepository.getSensorByName(sensor) instanceof ByteSensor) {
            byteSubSensorComboBox.setVisible(true);
            setupByteSubSensorComboBox(sensor);
        } else byteSubSensorComboBox.setVisible(false);

        String name = sensorRepository.getSensorByName(sensor).getName();
        String unit = sensorRepository.getSensorByName(sensor).getUnit();
        double maxRange = sensorRepository.getSensorByName(sensor).getMaxRange();
        double minRange = sensorRepository.getSensorByName(sensor).getMinRange();
        boolean isBoolean = sensorRepository.getSensorByName(sensor).isBoolean();
        String interpreterKey = sensorRepository.getSensorByName(sensor).getInterpreterKey();
        boolean hidden = sensorRepository.getSensorByName(sensor).isHidden();

        nameTextField.setText(name);
        unitTextField.setText(unit);
        maxRangeTextField.setText(String.valueOf(maxRange));
        minRangeTextField.setText(String.valueOf(minRange));
        isBooleanCheckbox.setSelected(isBoolean);
        interpreterKeyComboBox.getSelectionModel().select(interpreterKey);
        hiddenCheckBox.setSelected(hidden);
    }

    protected void setupByteSubSensorComboBox(String byteSensor) throws IllegalArgumentException {
        byteSubSensorComboBox.getItems().clear();
        if (sensorRepository.getSensorByName(byteSensor) instanceof ByteSensor) {
            int numberOfBits = ((ByteSensor) sensorRepository.getSensorByName(byteSensor)).numberOfBits();
            for (int i = 0; i < numberOfBits; i++) {
                byteSubSensorComboBox.getItems().add(String.valueOf(i));
            }
            byteSubSensorComboBox.getSelectionModel().selectFirst();
        }
        else throw new IllegalArgumentException(String.format("Sensor %s is not a ByteSensor but %s", byteSensor, sensorRepository.getSensorByName(byteSensor).getClass().getSimpleName()));
    }

    @FXML
    public void saveEdit() {
        sensorRepository = Configuration.getInstance().sensorRepository;
        String sensor_ = sensorListView.getSelectionModel().getSelectedItem();

        String name = nameTextField.getText();
        String unit = unitTextField.getText();
        double maxRange = Double.parseDouble(maxRangeTextField.getText());
        double minRange = Double.parseDouble(minRangeTextField.getText());
        boolean isBoolean = isBooleanCheckbox.isSelected();
        String interpreterKey = interpreterKeyComboBox.getSelectionModel().getSelectedItem();
        boolean hidden = hiddenCheckBox.isSelected();

        Sensor sensor = sensorRepository.getSensorByName(sensor_);
        sensor.setName(name);
        sensor.setUnit(unit);
        sensor.setMaxRange(maxRange);
        sensor.setMinRange(minRange);
        sensor.setBoolean(isBoolean);
        sensor.setInterpreter(Configuration.getInstance().interpreterRepository.getInterpreter(interpreterKey));
        sensor.setHidden(hidden);

        var config = Configuration.getInstance();
        ModelAsYamlService modelAsYamlService = new ModelAsYamlService();
        try {
            modelAsYamlService.saveToFile(ConfigurationSaveModel.getConfigurationSaveModel(Configuration.getInstance()), true);
            Configuration.getInstance().reloadConfigInstance(modelAsYamlService.readFromFile(new ConfigurationSaveModel(), true));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }
}

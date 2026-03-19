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
        byteSubSensorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateByteFields(newValue));
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

    protected void updateFields(String sensor_) {
        if (sensor_ == null || sensorRepository.getSensorByName(sensor_) == null) return;
        sensorRepository = Configuration.getInstance().sensorRepository;

        Sensor sensor;
        if (sensorRepository.getSensorByName(sensor_) instanceof ByteSensor) {
            byteSubSensorComboBox.setVisible(true);
            setupByteSubSensorComboBox(sensor_);
            sensor = ((ByteSensor) sensorRepository.getSensorByName(sensor_)).getSensors()[byteSubSensorComboBox.getSelectionModel().getSelectedIndex()];
        } else {
            byteSubSensorComboBox.setVisible(false);
            sensor = sensorRepository.getSensorByName(sensor_);
        }

        String name = sensor.getName();
        String unit = sensor.getUnit();
        double maxRange = sensor.getMaxRange();
        double minRange = sensor.getMinRange();
        boolean isBoolean = sensor.isBoolean();
        String interpreterKey = sensor.getInterpreterKey();
        boolean hidden = sensor.isHidden();

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

    protected void updateByteFields(String bit) throws IllegalArgumentException {
        if (bit == null || sensorListView.getSelectionModel().getSelectedItem() == null || sensorRepository.getSensorByName(sensorListView.getSelectionModel().getSelectedItem()) == null) return;
        sensorRepository = Configuration.getInstance().sensorRepository;

        if (sensorRepository.getSensorByName(sensorListView.getSelectionModel().getSelectedItem()) instanceof ByteSensor) {
            Sensor sensor = ((ByteSensor) sensorRepository.getSensorByName(sensorListView.getSelectionModel().getSelectedItem())).getSensors()[Integer.parseInt(bit)];

            String name = sensor.getName();
            String unit = sensor.getUnit();
            double maxRange = sensor.getMaxRange();
            double minRange = sensor.getMinRange();
            boolean isBoolean = sensor.isBoolean();
            String interpreterKey = sensor.getInterpreterKey();
            boolean hidden = sensor.isHidden();

            nameTextField.setText(name);
            unitTextField.setText(unit);
            maxRangeTextField.setText(String.valueOf(maxRange));
            minRangeTextField.setText(String.valueOf(minRange));
            isBooleanCheckbox.setSelected(isBoolean);
            interpreterKeyComboBox.getSelectionModel().select(interpreterKey);
            hiddenCheckBox.setSelected(hidden);
        } else throw new IllegalArgumentException(String.format("Sensor %s is not a ByteSensor but %s", sensorListView.getSelectionModel().getSelectedItem(), sensorRepository.getSensorByName(sensorListView.getSelectionModel().getSelectedItem()).getClass().getSimpleName()));
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

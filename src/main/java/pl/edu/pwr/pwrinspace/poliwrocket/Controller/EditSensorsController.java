package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXCheckBox;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ByteSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsYamlService;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

public class EditSensorsController extends AddExistingSensorController {
    @FXML
    protected AnchorPane mainPanel;
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
    @FXML
    protected JFXTextField multiBitSensorTextField;
    @FXML
    protected JFXComboBox<String> multiBitSensorDirectionComboBox;

    private LinkedHashMap<Field, Node> fieldsHashMap = new LinkedHashMap<>();

    @Override
    @FXML
    public void initialize() {
        super.initialize();

        sensorTypeFilter.getItems().add("ByteSensor");

        interpreterKeyComboBox.getItems().add("None");
        config.interpreterRepository.getRepositorySet().forEach((key, interpreter) -> interpreterKeyComboBox.getItems().add(key));
        interpreterKeyComboBox.getSelectionModel().selectFirst();

        multiBitSensorDirectionComboBox.getItems().addAll("ascending", "descending");
        multiBitSensorDirectionComboBox.getSelectionModel().selectFirst();

        sensorListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleSensorSelection(newValue));
        byteSubSensorComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> handleByteSensorSelection(newValue));
    }

    @Override
    protected void updateFilters() {
        sensorRepository = config.sensorRepository;
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

    protected void handleSensorSelection(String sensor_) {
        if (sensor_ == null) return;

        sensorRepository = config.sensorRepository;
        Sensor sensor;
        fieldsHashMap.forEach((field, node) -> mainPanel.getChildren().remove(node));
        if (sensorRepository.getSensorByName(sensor_) instanceof ByteSensor) {
            sensor = sensorRepository.getSensorByName(sensor_);
            setupByteSubSensorComboBox((ByteSensor) sensor);
            handleByteSensorSelection(sensor_);
        } else {
            changeByteSensorDetailsVisibility(false);
            sensor = sensorRepository.getSensorByName(sensor_);
            if (sensor.getClass() != Sensor.class) fieldsHashMap = createFieldHashMap(sensor);
            setupFields();
            updateFields(sensor);
        }
    }

    private void handleByteSensorSelection(String sensor_) {
        if (sensor_ == null) return;
        sensorRepository = config.sensorRepository;

        changeByteSensorDetailsVisibility(true);
        Sensor sensor = ((ByteSensor) getSelectedSensor()).getSensors()[getSelectedBit()];
        updateFields(sensor);
    }

    private void setupByteSubSensorComboBox(ByteSensor sensor) {
        byteSubSensorComboBox.getItems().clear();
        int numberOfBits = sensor.numberOfBits();
        for (int i = 0; i < numberOfBits; i++) {
            byteSubSensorComboBox.getItems().add(String.valueOf(i));
        }
        byteSubSensorComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    public void saveEdit() {
        try {
            sensorRepository = config.sensorRepository;
            Sensor sensor = getSelectedSensor();
            if (sensor instanceof ByteSensor) sensor = ((ByteSensor) sensor).getSensors()[getSelectedBit()];

            updateSensor(sensor);

            ModelAsYamlService modelAsYamlService = new ModelAsYamlService();
            modelAsYamlService.saveToFile(ConfigurationSaveModel.getConfigurationSaveModel(config), true);
            config.reloadConfigInstance(modelAsYamlService.readFromFile(new ConfigurationSaveModel(), true));
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private void updateFields(Sensor sensor) {
        sensorRepository = config.sensorRepository;
        String name = sensor.getName();
        String unit = sensor.getUnit();
        double maxRange = sensor.getMaxRange();
        double minRange = sensor.getMinRange();
        boolean isBoolean = sensor.isBoolean();
        String interpreterKey = sensor.getInterpreterKey();
        if (interpreterKey == null || interpreterKey.isEmpty()) interpreterKey = "None";
        boolean hidden = sensor.isHidden();

        if (!interpreterKey.equals("None") && !isBoolean) {
            logger.error("Sensor {} cannot have interpreter and not be boolean, setting to boolean", sensor.getName());
            isBoolean = true;
        }

        nameTextField.setText(name);
        unitTextField.setText(unit);
        maxRangeTextField.setText(String.valueOf(maxRange));
        minRangeTextField.setText(String.valueOf(minRange));
        isBooleanCheckbox.setSelected(isBoolean);
        interpreterKeyComboBox.getSelectionModel().select(interpreterKey);
        hiddenCheckBox.setSelected(hidden);
    }

    private void updateSensor(Sensor sensor) {
        String name = nameTextField.getText();
        String unit = unitTextField.getText();
        double maxRange = Double.parseDouble(maxRangeTextField.getText());
        double minRange = Double.parseDouble(minRangeTextField.getText());
        boolean isBoolean = isBooleanCheckbox.isSelected();
        String interpreterKey = interpreterKeyComboBox.getSelectionModel().getSelectedItem();
        boolean hidden = hiddenCheckBox.isSelected();

        sensor.setName(name);
        sensor.setUnit(unit);
        sensor.setMaxRange(maxRange);
        sensor.setMinRange(minRange);
        sensor.setBoolean(isBoolean);
        sensor.setInterpreter(config.interpreterRepository.getInterpreter(interpreterKey));
        sensor.setHidden(hidden);

    }

    private int getSelectedBit() {
        return byteSubSensorComboBox.getSelectionModel().getSelectedIndex();
    }

    private LinkedHashMap<Field, Node> createFieldHashMap(Sensor sensor) {
        LinkedHashMap<Field, Node> fields = new LinkedHashMap<>();
        Class<?> sensorClass = sensor.getClass();
        for (Field field : sensorClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(JsonProperty.class)) {
                if (field.getType().equals(boolean.class)) {
                    var node = new JFXCheckBox(field.getName());
                    fields.put(field, node);
                } else {
                    var node = new JFXTextField();
                    node.setPromptText(field.getName());
                    fields.put(field, node);
                }
            }
        }
        return fields;
    }

    private void setupFields() {
        int layoutX = 585;
        int layoutY = 295;

        for (Node node : fieldsHashMap.values()) {
            node.setLayoutX(layoutX);
            node.prefWidth(200);
            node.setLayoutY(layoutY);
            node.prefHeight(25);
            layoutY += 25;
            layoutY += 15;

            mainPanel.getChildren().add(node);
        }
    }

    private void changeByteSensorDetailsVisibility(boolean visible) {
        byteSubSensorComboBox.setVisible(visible);
        multiBitSensorTextField.setVisible(visible);
        multiBitSensorDirectionComboBox.setVisible(visible);
    }
}

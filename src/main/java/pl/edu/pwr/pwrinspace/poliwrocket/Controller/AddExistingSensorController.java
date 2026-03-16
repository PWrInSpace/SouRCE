package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.google.protobuf.Descriptors;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextArea;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.stage.Stage;
import org.reflections.Reflections;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.FrameProtos;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ByteSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.Sensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.SensorDestination;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.SensorRepository;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsYamlService;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddExistingSensorController extends BaseNewComponentController {
    @FXML
    JFXComboBox<String> sensorTypeFilter;
    @FXML
    JFXTextArea nameFilter;
    @FXML
    JFXComboBox<String> destinationComboBox;
    @FXML
    JFXListView<String> sensorListView;
    @FXML
    JFXButton addExistingSensorButton;

    FilteredList<String> filteredSensorList;

    HashMap<String, Tile> tileHashMap;
    HashMap<String, Indicator> indicatorHashMap;

    SensorRepository sensorRepository = Configuration.getInstance().sensorRepository;

    List<String> bannedSensorsList = new ArrayList<>();

    @FXML
    public void initialize() {
        sensorListView.setCellFactory(cell -> new ListCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                }
                else {
                    String listViewString = item + ", " + sensorRepository.getSensorByName(item).getListViewString();
                    setText(listViewString);
                }
            }
        });

        var sensorTypes = new ArrayList<Class<? extends Sensor>>();
        sensorTypes.add(Sensor.class);
        sensorTypes.addAll(new Reflections("pl.edu.pwr.pwrinspace").getSubTypesOf(Sensor.class));
        sensorTypes.remove(ByteSensor.class);
        for (Class<? extends Sensor> sensorClass : sensorTypes) sensorTypeFilter.getItems().add(sensorClass.getSimpleName());
        sensorTypeFilter.getSelectionModel().selectFirst();

        filteredSensorList = new FilteredList<>(FXCollections.observableList(new ArrayList<>(sensorRepository.getSensorsKeys())));
        sensorListView.setItems(filteredSensorList);

        List<Descriptors.FieldDescriptor> fields = FrameProtos.LoRaCommand.getDescriptor().getFields();
        for (Descriptors.FieldDescriptor field : fields) {
            bannedSensorsList.add(field.getName());
        }

        //ustawianie listeners
        sensorTypeFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        nameFilter.textProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        updateFilters();
    }

    @FXML
    private void addExistingSensor() {
        try {
            var modelAsYamlService = new ModelAsYamlService();

            var sensorDestination = getSensorDestination();
            var sensor = sensorRepository.getSensorByName(getSelectedSensor());
            modelAsYamlService.addSensorToController(new ConfigurationSaveModel(), sensor, sensorDestination);

            Configuration.getInstance().reloadConfigInstance(modelAsYamlService.readFromFile(new ConfigurationSaveModel(), true));

            ((Stage) addExistingSensorButton.getScene().getWindow()).close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private String getSelectedSensor() throws InvalidParameterException {
        if (sensorListView.getSelectionModel().getSelectedItem() != null) {
            return sensorListView.getSelectionModel().getSelectedItem();
        } else throw new InvalidParameterException("Sensor key not selected");
    }

    private SensorDestination getSensorDestination() {
        String destination = destinationComboBox.getSelectionModel().getSelectedItem();
        String destinationControllerName = parentController.getControllerName();
        return new SensorDestination(destination, destinationControllerName);
    }


    private void updateFilters() {
        String type = sensorTypeFilter.getSelectionModel().getSelectedItem();
        String name = nameFilter.getText();

        filteredSensorList.setPredicate(sensor -> {
            if (parentController != null) {
                for (String controllerName : sensorRepository.getSensorByName(sensor).getDestinationControllerNames()) {
                    if (controllerName.equals(parentController.getControllerName())) return false;
                }
            }
            if (type.equals("ByteSensor")) return false;
            if (bannedSensorsList.contains(sensor)) return false;
            if (!sensor.contains(name)) return false;
            return type.equals(sensorRepository.getSensorByName(sensor).getClass().getSimpleName());
        });
    }

    public void setTileHashMap(HashMap<String, Tile> tileHashMap) {
        this.tileHashMap = tileHashMap;
    }

    public void setIndicatorHashMap(HashMap<String, Indicator> indicatorHashMap) {
        this.indicatorHashMap = indicatorHashMap;
    }

    public void updateDestinationComboBox() {
        if (tileHashMap != null && indicatorHashMap != null) {
            for (String tileName : tileHashMap.keySet()) {
                if (!tileHashMap.get(tileName).isVisible()) destinationComboBox.getItems().add(tileName);
            }
            for (String indicatorName : indicatorHashMap.keySet()) {
                if (!indicatorHashMap.get(indicatorName).isVisible()) destinationComboBox.getItems().add(indicatorName);
            }
            destinationComboBox.getSelectionModel().selectFirst();
        }
    }
}

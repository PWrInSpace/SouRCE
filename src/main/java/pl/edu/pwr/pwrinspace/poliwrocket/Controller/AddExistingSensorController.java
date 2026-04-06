package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.google.protobuf.Descriptors;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXTextField;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    protected JFXComboBox<String> sensorTypeFilter;
    @FXML
    protected JFXTextField nameFilter;
    @FXML
    protected JFXComboBox<String> destinationComboBox;
    @FXML
    protected JFXListView<String> sensorListView;
    @FXML
    protected JFXButton addExistingSensorButton;

    protected ObservableList<String> observableSensorList;
    protected FilteredList<String> filteredSensorList;
    protected List<String> bannedSensorsList = new ArrayList<>();

    protected HashMap<String, Tile> tileHashMap;
    protected HashMap<String, Indicator> indicatorHashMap;

    protected Configuration config = Configuration.getInstance();
    protected SensorRepository sensorRepository;

    @FXML
    public void initialize() {
        config.addListener(this);
        sensorRepository = config.sensorRepository;

        // ustawiane opcji wyświetlania
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

        // ustawianie listy filtrów
        var sensorTypes = new ArrayList<Class<? extends Sensor>>();
        sensorTypes.add(Sensor.class);
        sensorTypes.addAll(new Reflections("pl.edu.pwr.pwrinspace").getSubTypesOf(Sensor.class));
        sensorTypes.remove(ByteSensor.class);
        for (Class<? extends Sensor> sensorClass : sensorTypes) sensorTypeFilter.getItems().add(sensorClass.getSimpleName());
        sensorTypeFilter.getSelectionModel().selectFirst();

        // inicjalizacja filtrowanej listy
        observableSensorList = FXCollections.observableArrayList();
        filteredSensorList = new FilteredList<>(observableSensorList);
        sensorListView.setItems(filteredSensorList);
        refreshSensorList();

        List<Descriptors.FieldDescriptor> fields = FrameProtos.LoRaCommand.getDescriptor().getFields();
        for (Descriptors.FieldDescriptor field : fields) {
            bannedSensorsList.add(field.getName());
        }

        //ustawianie listeners
        sensorTypeFilter.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        nameFilter.textProperty().addListener((observable, oldValue, newValue) -> updateFilters());
        updateFilters();
    }

    @Override
    public void invalidated(javafx.beans.Observable observable) {
        if (observable != config) return;

        Runnable refreshAction = () -> {
            if (observableSensorList == null || filteredSensorList == null) return;
            refreshSensorList();
            updateFilters();
        };

        if (Platform.isFxApplicationThread()) refreshAction.run();
        else Platform.runLater(refreshAction);
    }

    protected void refreshSensorList() {
        sensorRepository = config.sensorRepository;
        String selectedSensor = sensorListView.getSelectionModel().getSelectedItem();
        observableSensorList.setAll(sensorRepository.getSensorsKeys());
        if (selectedSensor != null && observableSensorList.contains(selectedSensor)) {
            sensorListView.getSelectionModel().select(selectedSensor);
        } else {
            sensorListView.getSelectionModel().clearSelection();
        }
    }

    @FXML
    private void addExistingSensor() {
        try {
            var modelAsYamlService = new ModelAsYamlService();

            var sensorDestination = getSensorDestination();
            var sensor = getSelectedSensor();
            modelAsYamlService.addSensorToController(new ConfigurationSaveModel(), sensor, sensorDestination);

            config.reloadConfigInstance(modelAsYamlService.readFromFile(new ConfigurationSaveModel(), true));

            ((Stage) addExistingSensorButton.getScene().getWindow()).close();
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    protected Sensor getSelectedSensor() throws InvalidParameterException {
        sensorRepository = config.sensorRepository;

        if (sensorListView.getSelectionModel().getSelectedItem() != null) {
            return sensorRepository.getSensorByName(sensorListView.getSelectionModel().getSelectedItem());
        } else throw new InvalidParameterException("Sensor key not selected");
    }

    private SensorDestination getSensorDestination() {
        String destination = destinationComboBox.getSelectionModel().getSelectedItem();
        String destinationControllerName = parentController.getControllerName();
        return new SensorDestination(destination, destinationControllerName);
    }

    protected void updateFilters() {
        sensorRepository = config.sensorRepository;
        String type = sensorTypeFilter.getSelectionModel().getSelectedItem();
        String name = nameFilter.getText();

        filteredSensorList.setPredicate(sensor -> {
            if (type == null) return false;
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
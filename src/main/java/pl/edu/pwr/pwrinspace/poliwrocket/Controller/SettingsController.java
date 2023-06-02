package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ITare;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsJsonSaveService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SettingsController extends BasicController {

    @FXML
    private AnchorPane mainPanel;

    @FXML
    protected JFXButton applyButton;

    @FXML
    protected JFXButton reloadButton;

    private final ModelAsJsonSaveService modelAsJsonSaveService = new ModelAsJsonSaveService();

    List<IAction> actionList = new ArrayList<>();

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @FXML
    protected void initialize() {
        int initYLabel = 49;
        int initYInput = 45;
        int offsetY = 40;

        for (ISensor sensor : Configuration.getInstance().sensorRepository.getAllBasicSensors().values()) {
            if (sensor instanceof ITare) {
                var tareSensor = (ITare) sensor;
                Label label = new Label(tareSensor.getName());
                JFXTextField input = new JFXTextField(Double.toString(tareSensor.getTareValue()));
                label.setLayoutX(42);
                label.setLayoutY(initYLabel);
                label.setPrefHeight(18);
                label.setPrefWidth(110);
                input.setLayoutX(215);
                input.setLayoutY(initYInput);
                input.setPrefHeight(26);
                input.setPrefWidth(150);
                mainPanel.getChildren().add(label);
                mainPanel.getChildren().add(input);
                actionList.add(() -> {
                    try {
                        double value = Double.parseDouble(input.getText());
                        tareSensor.setTareValue(value);
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }
                });
                initYLabel += offsetY;
                initYInput += offsetY;
            }
        }

        applyButton.setOnMouseClicked(event -> executorService.execute(this::apply));
        reloadButton.setOnMouseClicked(event -> executorService.execute(this::reloadConfig));
    }

    void apply() {
        actionList.forEach(IAction::execute);
    }

    void reloadConfig() {
        try {
            Configuration.getInstance().reloadConfigInstance(modelAsJsonSaveService.readFromFile(new ConfigurationSaveModel()));
        } catch (Exception e) {
            logger.error("Bad config file");
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            logger.error(e.toString());
        }
    }

    @Override
    public void invalidated(Observable observable) {

    }
}

package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ITare;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ModelAsJsonSaveService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingsController {

    @FXML
    private AnchorPane mainPanel;

    @FXML
    private Button applyButton;

    @FXML
    private Button reloadButton;

    private static final Logger logger = LoggerFactory.getLogger(SettingsController.class);

    private final ModelAsJsonSaveService modelAsJsonSaveService = new ModelAsJsonSaveService();

    List<IAction> actionList = new ArrayList<>();


    @FXML
    void initialize() {
        int initYLabel = 49;
        int initYInput = 45;
        int offsetY = 40;

        for (ISensor sensor : Configuration.getInstance().sensorRepository.getAllBasicSensors().values()) {
            if (sensor instanceof ITare) {
                var tareSensor = (ITare) sensor;
                Label label = new Label(tareSensor.getName());
                TextField input = new TextField(Double.toString(tareSensor.getTareValue()));
                label.setLayoutX(42);
                label.setLayoutY(initYLabel);
                label.setPrefHeight(18);
                label.setPrefWidth(125);
                input.setLayoutX(252);
                input.setLayoutY(initYInput);
                input.setPrefHeight(26);
                input.setPrefWidth(170);
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

        applyButton.setOnMouseClicked(event -> apply());
        reloadButton.setOnMouseClicked(event -> reloadConfig());
    }

    void apply() {
        actionList.forEach(IAction::execute);
    }

    void reloadConfig() {
        try {
            Configuration.getInstance().reloadConfigInstance((ConfigurationSaveModel) modelAsJsonSaveService.readFromFile(new ConfigurationSaveModel()));
        } catch (Exception e) {
            logger.error("Bad config file");
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            logger.error(e.toString());

        }
    }

}

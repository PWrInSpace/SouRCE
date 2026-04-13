package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.jfoenix.controls.JFXButton;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.FillingLevelSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;

import java.io.IOException;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.HashMap;
import java.util.LinkedList;

public abstract class BaseTilesFXSensorController extends BaseSensorController {
    private static final int _duration = 30;

    private static final Duration DURATION = Duration.ofSeconds(_duration);

    protected final HashMap<String, Tile> tileHashMap = new HashMap<>();
    protected final HashMap<String, Indicator> indicatorHashMap = new HashMap<>();
    protected final HashMap<String, Label> labelHashMap = new HashMap<>();

    @Override
    protected void buildVisualizationMap() {
        tileHashMap.clear();
        indicatorHashMap.clear();
        labelHashMap.clear();

        var fields = getAllFields(new LinkedList<>(), this.getClass());

        for (Field declaredField : fields) {
            try {
                if (declaredField.getType().isAssignableFrom(Tile.class)) {
                    ((Tile) declaredField.get(this)).setVisible(false);
                    tileHashMap.put(declaredField.getName(), (Tile) declaredField.get(this));

                } else if (declaredField.getType().isAssignableFrom(Indicator.class)) {
                    ((Indicator) declaredField.get(this)).setVisible(false);
                    indicatorHashMap.put(declaredField.getName(), (Indicator) declaredField.get(this));
                    var label = fields.stream().filter(f -> f.getName().equals("indicatorLabel" + declaredField.getName().charAt(declaredField.getName().length() - 1))).findFirst();

                    if (label.isPresent()) {
                        Label label_ = (Label) label.get().get(this);
                        label_.setVisible(false);
                        labelHashMap.put(declaredField.getName(), label_);
                    } else {
                        logger.error("Indicator without label!");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace(); //todo ustalić czy getMessage czy logger.error
            }
        }

        generateAddExistingSensorButton(0, 0);
    }

    @Override
    protected void setUIBySensors() {
        for (ISensor sensor : sensors) {
            var tile = tileHashMap.get(sensor.getDestination(getControllerName()));
            var indicator = indicatorHashMap.get(sensor.getDestination(getControllerName()));
            var label = labelHashMap.get(sensor.getDestination(getControllerName()));
            if (tile != null) {
                tile.setVisible(true);

                if(sensor.getMinRange() != sensor.getMaxRange()) {
                    tile.setMinValue(sensor.getMinRange());
                    tile.setMaxValue(sensor.getMaxRange());
                } else {
                    tile.setAutoScale(true);
                    tile.setMinValue(Double.MIN_VALUE);
                    tile.setMaxValue(Double.MAX_VALUE);
                }
                
                tile.setTitle(sensor.getName());
                tile.setUnit(sensor.getUnit());
                tile.setAverageVisible(true);
                tile.setSmoothing(true);
                tile.setTimePeriod(DURATION);
                tile.setAveragingPeriod(_duration);
                tile.setTextVisible(true);
                if(sensor instanceof FillingLevelSensor) {
                    tile.setSkinType(Tile.SkinType.FLUID);
                }
            } else if (indicator != null) {
                indicator.setVisible(true);

                if (label != null) {
                    label.setText(sensor.getName());
                    label.setVisible(true);
                }
            } else {
                logger.error("Wrong UI binding - destination not found: {}", sensor.getDestination(getControllerName()));
            }
        }
    }

    protected void generateAddExistingSensorButton(int layoutX, int layoutY) {
        AnchorPane mainPanel = null;
        try {
            Field mainPanelField = getClass().getDeclaredField("mainPanel");
            mainPanelField.setAccessible(true);
            Object value = mainPanelField.get(this);
            if (value instanceof AnchorPane) {
                mainPanel = (AnchorPane) value;
            } else if (value != null) {
                logger.error("Field 'mainPanel' exists but is not AnchorPane in {}", getClass().getSimpleName());
            }
        } catch (NoSuchFieldException e) {
            logger.warn("Field 'mainPanel' not found in {}", getClass().getSimpleName());
        } catch (IllegalAccessException e) {
            logger.error("Cannot access field 'mainPanel' in {}", getClass().getSimpleName(), e);
        }

        if (mainPanel == null) {
            logger.warn("Cannot add '+S' button because mainPanel is null in {}", getClass().getSimpleName());
            return;
        }

        var addExistingSensorButton = new JFXButton("+S");
        addExistingSensorButton.setLayoutX(layoutX);
        addExistingSensorButton.setLayoutY(layoutY);
        mainPanel.getChildren().add(addExistingSensorButton);
        addExistingSensorButton.setOnAction(event -> {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AddExistingSensorView.fxml"));
            Stage popupStage = new Stage();
            try {
                Parent root = loader.load();
                Scene popupScene = new Scene(root);
                AddExistingSensorController popupController = loader.getController();
                popupController.setParentController(this);
                popupController.setTileHashMap(tileHashMap);
                popupController.setIndicatorHashMap(indicatorHashMap);
                popupController.updateDestinationComboBox();
                popupStage.setScene(popupScene);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            popupStage.initOwner(addExistingSensorButton.getScene().getWindow());
            popupStage.initModality(Modality.WINDOW_MODAL);
            popupStage.setResizable(false);

            popupStage.showAndWait();
        });
    }
}

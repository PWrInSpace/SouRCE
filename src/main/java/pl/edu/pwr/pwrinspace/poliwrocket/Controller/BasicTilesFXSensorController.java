package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.scene.control.Label;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.FillingLevelSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;

import java.lang.reflect.Field;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;

public abstract class BasicTilesFXSensorController extends BasicSensorController {

    private static final int _duration = 30;

    private static final Duration DURATION = Duration.ofSeconds(_duration);

    protected final HashMap<String, Tile> tileHashMap = new HashMap<>();
    protected final HashMap<String, Indicator> indicatorHashMap = new HashMap<>();
    protected final HashMap<String, Label> labelHashMap = new HashMap<>();

    protected void buildVisualizationMap() {
        tileHashMap.clear();
        indicatorHashMap.clear();
        labelHashMap.clear();
        for (Field declaredField : this.getClass().getDeclaredFields()) {
            try {
                if (declaredField.getType().isAssignableFrom(Tile.class)) {
                    ((Tile) declaredField.get(this)).setVisible(false);
                    tileHashMap.put(declaredField.getName(), (Tile) declaredField.get(this));

                } else if (declaredField.getType().isAssignableFrom(Indicator.class)) {
                    ((Indicator) declaredField.get(this)).setVisible(false);
                    indicatorHashMap.put(declaredField.getName(), (Indicator) declaredField.get(this));
                    var label = Arrays.stream(this.getClass().getDeclaredFields()).filter(f -> f.getName().equals("indicatorLabel" + declaredField.getName().charAt(declaredField.getName().length() - 1))).findFirst();
                    if(label.isPresent()) {
                        labelHashMap.put(declaredField.getName(), (Label) label.get().get(this));
                    } else {
                        logger.error("Indicator without label!");
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    protected void setUIBySensors() {
        for (ISensor sensor : sensors) {
            var tile = tileHashMap.get(sensor.getDestination());
            var indicator = indicatorHashMap.get(sensor.getDestination());
            var label = labelHashMap.get(sensor.getDestination());
            if (tile != null) {
                tile.setVisible(true);
                tile.setMaxValue(sensor.getMaxRange());
                tile.setMinValue(sensor.getMinRange());
                tile.setTitle(sensor.getName());
                tile.setUnit(sensor.getUnit());
                tile.setAverageVisible(true);
                tile.setSmoothing(true);
                tile.setTimePeriod(DURATION);
                tile.setAveragingPeriod(_duration);
                if(sensor instanceof FillingLevelSensor) {
                    tile.setSkinType(Tile.SkinType.FLUID);
                }
                //tile.setAveragingPeriod(_duration * Configuration.getInstance().FPS);
            } else if (indicator != null) {
                indicator.setVisible(true);

                if (label != null) {
                    label.setText(sensor.getName());
                    label.setVisible(true);
                }
            } else {
                logger.error("Wrong UI binding - destination not found: {}", sensor.getDestination());
            }
        }
    }

}

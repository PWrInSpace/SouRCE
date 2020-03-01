package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.chart.ChartData;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class DataController implements InvalidationListener {

    private static Duration DURATION = Duration.ofSeconds(10);

    @FXML
    private Tile dataGauge1;

    @FXML
    private Tile dataGauge2;

    @FXML
    private Tile dataGauge3;

    @FXML
    private Tile dataGauge4;

    @FXML
    private Tile dataGauge5;

    @FXML
    private Tile dataGauge6;

    @FXML
    private Tile dataGauge7;

    @FXML
    private Tile dataGauge8;

    HashMap<String,Tile> tileHashSet = new HashMap<>();

    HashSet<Sensor> sensors = new HashSet<>();

    @FXML
    void initialize() {
        tileHashSet.put(dataGauge1.getId(),dataGauge1);
        tileHashSet.put(dataGauge2.getId(),dataGauge2);
        tileHashSet.put(dataGauge3.getId(),dataGauge3);
        tileHashSet.put(dataGauge4.getId(),dataGauge4);
        tileHashSet.put(dataGauge5.getId(),dataGauge5);
        tileHashSet.put(dataGauge6.getId(),dataGauge6);
        tileHashSet.put(dataGauge7.getId(),dataGauge7);
        tileHashSet.put(dataGauge8.getId(),dataGauge8);
    }

    public void injectModel(Collection<Sensor> sensors){
        this.sensors.addAll(sensors);
        setTilesUI();
    }
    public void setTilesUI(){
        for(Sensor s:sensors) {
            var tile = tileHashSet.get(s.getDestination());
            if(tile!=null){
                tile.setVisible(true);
                tile.setMaxValue(s.getMaxRange());
                tile.setMinValue(s.getMinRange());
                tile.setTitle(s.getName());
                tile.setUnit(s.getUnit());
                tile.setAverageVisible(true);
                tile.setMaxTimePeriod(DURATION);
                tile.setTimePeriodResolution(TimeUnit.SECONDS);
                tile.setSnapToTicks(true);
                tile.setSmoothing(true);
                tile.setTimePeriod(DURATION);
                //tile.setAveragingPeriod(30);

            }
        }

    }
    @Override
    public void invalidated(Observable observable) {

//        dataGauge1.addChartData(new ChartData(new Random().nextDouble()*10));
//        dataGauge2.addChartData(new ChartData(new Random().nextDouble()*10));
//        dataGauge3.addChartData(new ChartData(new Random().nextDouble()*10));
//        dataGauge2.setValue(new Random().nextDouble()*100);
//        dataGauge3.setValue(new Random().nextDouble()*100);
//        dataGauge4.setValue(new Random().nextDouble()*100);



        try{
            var sensor = ((Sensor) observable);
            Platform.runLater(new Thread( () -> tileHashSet.get(sensor.getDestination()).setValue(sensor.getValue()))); //tileHashSet.get(sensor.getDestination()).setValue(sensor.getValue());
            //Platform.runLater(new Thread(() -> tileHashSet.get(sensor.getDestination()).addChartData(new ChartData(sensor.getValue(),sensor.getTimeStamp()))));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public Tile getDataGauge1() {
        return dataGauge1;
    }

    public Tile getDataGauge2() {
        return dataGauge2;
    }

    public Tile getDataGauge3() {
        return dataGauge3;
    }

    public Tile getDataGauge4() {
        return dataGauge4;
    }

    public Tile getDataGauge5() {
        return dataGauge5;
    }

    public Tile getDataGauge6() {
        return dataGauge6;
    }

    public Tile getDataGauge7() {
        return dataGauge7;
    }

    public Tile getDataGauge8() {
        return dataGauge8;
    }
}

package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.interactivemesh.jfx.importer.ModelImporter;
import com.interactivemesh.jfx.importer.tds.TdsModelImporter;
import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.addons.Indicator;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;

import java.time.Duration;
import java.util.HashMap;
import java.util.Random;

public class FlyControlController implements InvalidationListener, MapComponentInitializedListener {
    private SmartGroup root = new SmartGroup();
    private  Node rocket3DModel;

    @FXML
    private SubScene subScene;

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

    @FXML
    private Indicator valveIndicator1;

    @FXML
    private Label valveLabel1;

    @FXML
    private Button valveOpenButton1;

    @FXML
    private Button valveCloseButton1;

    @FXML
    private Indicator valveIndicator2;

    @FXML
    private Label valveLabel2;

    @FXML
    private Button valveOpenButton2;

    @FXML
    private Button valveCloseButton2;

    @FXML
    private Indicator valveIndicator3;

    @FXML
    private Label valveLabel3;

    @FXML
    private Button valveOpenButton3;

    @FXML
    private Button valveCloseButton3;

    @FXML
    private Indicator valveIndicator4;

    @FXML
    private Label valveLabel4;

    @FXML
    private Button valveOpenButton4;

    @FXML
    private Button valveCloseButton4;

    @FXML
    private Indicator valveIndicator5;

    @FXML
    private Label valveLabel5;

    @FXML
    private Button valveOpenButton5;

    @FXML
    private Button valveCloseButton5;

    @FXML
    private Gauge dataGauge9;

    @FXML
    private Gauge dataGauge10;

    @FXML
    private Indicator dataIndicator1;

    @FXML
    private Indicator dataIndicator2;

    @FXML
    private Indicator dataIndicator4;

    @FXML
    private Indicator dataIndicator3;

    @FXML
    private Gauge powerGauge4;

    @FXML
    private Gauge powerGauge5;

    @FXML
    private Gauge powerGauge6;

    @FXML
    private Gauge powerGauge3;

    @FXML
    private Gauge powerGauge7;

    @FXML
    private Gauge powerGauge2;

    @FXML
    private Gauge powerGauge1;

    @FXML
    private Label powerLabel7;

    @FXML
    private Label powerLabel6;

    @FXML
    private Label powerLabel5;

    @FXML
    private Label powerLabel4;

    @FXML
    private Label powerLabel3;

    @FXML
    private Label powerLabel2;

    @FXML
    private Label powerLabel1;

    @FXML
    private Tile dataTile1;

    @FXML
    private GoogleMapView mapView;

    private GoogleMap map;

    public HashMap<Object, Gauge> sensors = new HashMap<Object, Gauge>();

    public FlyControlController() {

    }

    @FXML
    void initialize() {
        assert dataGauge1 != null : "fx:id=\"dataGauge1\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert dataGauge2 != null : "fx:id=\"dataGauge2\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert dataGauge3 != null : "fx:id=\"dataGauge3\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert dataGauge4 != null : "fx:id=\"dataGauge4\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert dataGauge5 != null : "fx:id=\"dataGauge5\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert dataGauge6 != null : "fx:id=\"dataGauge6\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert dataGauge7 != null : "fx:id=\"dataGauge7\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert dataGauge8 != null : "fx:id=\"dataGauge8\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveIndicator1 != null : "fx:id=\"valveIndicator1\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveLabel1 != null : "fx:id=\"valveLabel1\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveOpenButton1 != null : "fx:id=\"valveOpenButton1\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveCloseButton1 != null : "fx:id=\"valveCloseButton1\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveIndicator2 != null : "fx:id=\"valveIndicator2\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveLabel2 != null : "fx:id=\"valveLabel2\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveOpenButton2 != null : "fx:id=\"valveOpenButton2\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveCloseButton2 != null : "fx:id=\"valveCloseButton2\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveIndicator3 != null : "fx:id=\"valveIndicator3\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveLabel3 != null : "fx:id=\"valveLabel3\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveOpenButton3 != null : "fx:id=\"valveOpenButton3\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveCloseButton3 != null : "fx:id=\"valveCloseButton3\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveIndicator4 != null : "fx:id=\"valveIndicator4\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveLabel4 != null : "fx:id=\"valveLabel4\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveOpenButton4 != null : "fx:id=\"valveOpenButton4\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveCloseButton4 != null : "fx:id=\"valveCloseButton4\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveIndicator5 != null : "fx:id=\"valveIndicator5\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveLabel5 != null : "fx:id=\"valveLabel5\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveOpenButton5 != null : "fx:id=\"valveOpenButton5\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert valveCloseButton5 != null : "fx:id=\"valveCloseButton5\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert dataGauge9 != null : "fx:id=\"dataGauge9\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert dataGauge10 != null : "fx:id=\"dataGauge10\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert dataIndicator1 != null : "fx:id=\"dataIndicator1\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert dataIndicator2 != null : "fx:id=\"dataIndicator2\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert dataIndicator4 != null : "fx:id=\"dataIndicator4\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert dataIndicator3 != null : "fx:id=\"dataIndicator3\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge4 != null : "fx:id=\"powerGauge4\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge5 != null : "fx:id=\"powerGauge5\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge6 != null : "fx:id=\"powerGauge6\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge3 != null : "fx:id=\"powerGauge3\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge7 != null : "fx:id=\"powerGauge7\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge2 != null : "fx:id=\"powerGauge2\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerGauge1 != null : "fx:id=\"powerGauge1\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel7 != null : "fx:id=\"powerLabel7\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel6 != null : "fx:id=\"powerLabel6\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel5 != null : "fx:id=\"powerLabel5\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel4 != null : "fx:id=\"powerLabel4\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel3 != null : "fx:id=\"powerLabel3\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel2 != null : "fx:id=\"powerLabel2\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        assert powerLabel1 != null : "fx:id=\"powerLabel1\" was not injected: check your FXML file 'FlyControlView.fxml'.";

        assert dataTile1 != null : "fx:id=\"dataTile1\" was not injected: check your FXML file 'FlyControlView.fxml'.";

        dataTile1.setMaxTimePeriod(Duration.ofSeconds(15));
        dataTile1.setMaxValue(1000.0);
        //dataTile1.setStrokeWithGradient(true);
        dataTile1.setSmoothing(true);

        assert mapView != null : "fx:id=\"mapView\" was not injected: check your FXML file 'FlyControlView.fxml'.";
        mapView.addMapInializedListener(this);

        //ChartData c = new ChartData();
        //dataTile1.addChartData(c);

        //Creating camera - i don`t know why but this is in exmaple
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-700);
        camera.setNearClip(0.1);
        camera.setFarClip(3000.0);
        camera.setFieldOfView(60);
        subScene.setCamera(camera);


/*
        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(0);
        light.setTranslateY(-3000);
        light.setTranslateZ(-1600);
        root.getChildren().add(light);

        AmbientLight ambiance = new AmbientLight(Color.WHITE);
        root.getChildren().add(ambiance);
*/
        //setting background color
        subScene.setFill(Color.SKYBLUE);

        //importing 3ds model
        ModelImporter tdsImporter = new TdsModelImporter();
        tdsImporter.read(getClass().getClassLoader().getResource("rocketModel.3ds"));
        Node[] tdsMesh = (Node[]) tdsImporter.getImport();
        rocket3DModel = tdsMesh[0];
        tdsImporter.close();

        root.getChildren().add(rocket3DModel);
        subScene.setRoot(root);
    }

    public Tile getDataGauge2() {
        return dataGauge2;
    }

    public void setDataGauge2(Tile dataGauge2) {
        this.dataGauge2 = dataGauge2;
    }

    public Tile getDataGauge3() {
        return dataGauge3;
    }

    public void setDataGauge3(Tile dataGauge3) {
        this.dataGauge3 = dataGauge3;
    }

    public Tile getDataGauge4() {
        return dataGauge4;
    }

    public void setDataGauge4(Tile dataGauge4) {
        this.dataGauge4 = dataGauge4;
    }

    public Tile getDataGauge5() {
        return dataGauge5;
    }

    public void setDataGauge5(Tile dataGauge5) {
        this.dataGauge5 = dataGauge5;
    }

    public Tile getDataGauge6() {
        return dataGauge6;
    }

    public void setDataGauge6(Tile dataGauge6) {
        this.dataGauge6 = dataGauge6;
    }

    public Tile getDataGauge7() {
        return dataGauge7;
    }

    public void setDataGauge7(Tile dataGauge7) {
        this.dataGauge7 = dataGauge7;
    }

    public Tile getDataGauge8() {
        return dataGauge8;
    }

    public void setDataGauge8(Tile dataGauge8) {
        this.dataGauge8 = dataGauge8;
    }

    public Indicator getValveIndicator1() {
        return valveIndicator1;
    }

    public void setValveIndicator1(Indicator valveIndicator1) {
        this.valveIndicator1 = valveIndicator1;
    }

    public Label getValveLabel1() {
        return valveLabel1;
    }

    public void setValveLabel1(Label valveLabel1) {
        this.valveLabel1 = valveLabel1;
    }

    public Button getValveOpenButton1() {
        return valveOpenButton1;
    }

    public void setValveOpenButton1(Button valveOpenButton1) {
        this.valveOpenButton1 = valveOpenButton1;
    }

    public Button getValveCloseButton1() {
        return valveCloseButton1;
    }

    public void setValveCloseButton1(Button valveCloseButton1) {
        this.valveCloseButton1 = valveCloseButton1;
    }

    public Indicator getValveIndicator2() {
        return valveIndicator2;
    }

    public void setValveIndicator2(Indicator valveIndicator2) {
        this.valveIndicator2 = valveIndicator2;
    }

    public Label getValveLabel2() {
        return valveLabel2;
    }

    public void setValveLabel2(Label valveLabel2) {
        this.valveLabel2 = valveLabel2;
    }

    public Button getValveOpenButton2() {
        return valveOpenButton2;
    }

    public void setValveOpenButton2(Button valveOpenButton2) {
        this.valveOpenButton2 = valveOpenButton2;
    }

    public Button getValveCloseButton2() {
        return valveCloseButton2;
    }

    public void setValveCloseButton2(Button valveCloseButton2) {
        this.valveCloseButton2 = valveCloseButton2;
    }

    public Indicator getValveIndicator3() {
        return valveIndicator3;
    }

    public void setValveIndicator3(Indicator valveIndicator3) {
        this.valveIndicator3 = valveIndicator3;
    }

    public Label getValveLabel3() {
        return valveLabel3;
    }

    public void setValveLabel3(Label valveLabel3) {
        this.valveLabel3 = valveLabel3;
    }

    public Button getValveOpenButton3() {
        return valveOpenButton3;
    }

    public void setValveOpenButton3(Button valveOpenButton3) {
        this.valveOpenButton3 = valveOpenButton3;
    }

    public Button getValveCloseButton3() {
        return valveCloseButton3;
    }

    public void setValveCloseButton3(Button valveCloseButton3) {
        this.valveCloseButton3 = valveCloseButton3;
    }

    public Indicator getValveIndicator4() {
        return valveIndicator4;
    }

    public void setValveIndicator4(Indicator valveIndicator4) {
        this.valveIndicator4 = valveIndicator4;
    }

    public Label getValveLabel4() {
        return valveLabel4;
    }

    public void setValveLabel4(Label valveLabel4) {
        this.valveLabel4 = valveLabel4;
    }

    public Button getValveOpenButton4() {
        return valveOpenButton4;
    }

    public void setValveOpenButton4(Button valveOpenButton4) {
        this.valveOpenButton4 = valveOpenButton4;
    }

    public Button getValveCloseButton4() {
        return valveCloseButton4;
    }

    public void setValveCloseButton4(Button valveCloseButton4) {
        this.valveCloseButton4 = valveCloseButton4;
    }

    public Indicator getValveIndicator5() {
        return valveIndicator5;
    }

    public void setValveIndicator5(Indicator valveIndicator5) {
        this.valveIndicator5 = valveIndicator5;
    }

    public Label getValveLabel5() {
        return valveLabel5;
    }

    public void setValveLabel5(Label valveLabel5) {
        this.valveLabel5 = valveLabel5;
    }

    public Button getValveOpenButton5() {
        return valveOpenButton5;
    }

    public void setValveOpenButton5(Button valveOpenButton5) {
        this.valveOpenButton5 = valveOpenButton5;
    }

    public Button getValveCloseButton5() {
        return valveCloseButton5;
    }

    public void setValveCloseButton5(Button valveCloseButton5) {
        this.valveCloseButton5 = valveCloseButton5;
    }

    public Gauge getDataGauge9() {
        return dataGauge9;
    }

    public void setDataGauge9(Gauge dataGauge9) {
        this.dataGauge9 = dataGauge9;
    }

    public Gauge getDataGauge10() {
        return dataGauge10;
    }

    public void setDataGauge10(Gauge dataGauge10) {
        this.dataGauge10 = dataGauge10;
    }

    public Indicator getDataIndicator1() {
        return dataIndicator1;
    }

    public void setDataIndicator1(Indicator dataIndicator1) {
        this.dataIndicator1 = dataIndicator1;
    }

    public Indicator getDataIndicator2() {
        return dataIndicator2;
    }

    public void setDataIndicator2(Indicator dataIndicator2) {
        this.dataIndicator2 = dataIndicator2;
    }

    public Indicator getDataIndicator4() {
        return dataIndicator4;
    }

    public void setDataIndicator4(Indicator dataIndicator4) {
        this.dataIndicator4 = dataIndicator4;
    }

    public Indicator getDataIndicator3() {
        return dataIndicator3;
    }

    public void setDataIndicator3(Indicator dataIndicator3) {
        this.dataIndicator3 = dataIndicator3;
    }

    public Gauge getPowerGauge4() {
        return powerGauge4;
    }

    public void setPowerGauge4(Gauge powerGauge4) {
        this.powerGauge4 = powerGauge4;
    }

    public Gauge getPowerGauge5() {
        return powerGauge5;
    }

    public void setPowerGauge5(Gauge powerGauge5) {
        this.powerGauge5 = powerGauge5;
    }

    public Gauge getPowerGauge6() {
        return powerGauge6;
    }

    public void setPowerGauge6(Gauge powerGauge6) {
        this.powerGauge6 = powerGauge6;
    }

    public Gauge getPowerGauge3() {
        return powerGauge3;
    }

    public void setPowerGauge3(Gauge powerGauge3) {
        this.powerGauge3 = powerGauge3;
    }

    public Gauge getPowerGauge7() {
        return powerGauge7;
    }

    public void setPowerGauge7(Gauge powerGauge7) {
        this.powerGauge7 = powerGauge7;
    }

    public Gauge getPowerGauge2() {
        return powerGauge2;
    }

    public void setPowerGauge2(Gauge powerGauge2) {
        this.powerGauge2 = powerGauge2;
    }

    public Gauge getPowerGauge1() {
        return powerGauge1;
    }

    public void setPowerGauge1(Gauge powerGauge1) {
        this.powerGauge1 = powerGauge1;
    }

    public Label getPowerLabel7() {
        return powerLabel7;
    }

    public void setPowerLabel7(Label powerLabel7) {
        this.powerLabel7 = powerLabel7;
    }

    public Label getPowerLabel6() {
        return powerLabel6;
    }

    public void setPowerLabel6(Label powerLabel6) {
        this.powerLabel6 = powerLabel6;
    }

    public Label getPowerLabel5() {
        return powerLabel5;
    }

    public void setPowerLabel5(Label powerLabel5) {
        this.powerLabel5 = powerLabel5;
    }

    public Label getPowerLabel4() {
        return powerLabel4;
    }

    public void setPowerLabel4(Label powerLabel4) {
        this.powerLabel4 = powerLabel4;
    }

    public Label getPowerLabel3() {
        return powerLabel3;
    }

    public void setPowerLabel3(Label powerLabel3) {
        this.powerLabel3 = powerLabel3;
    }

    public Label getPowerLabel2() {
        return powerLabel2;
    }

    public void setPowerLabel2(Label powerLabel2) {
        this.powerLabel2 = powerLabel2;
    }

    public Label getPowerLabel1() {
        return powerLabel1;
    }

    public void setPowerLabel1(Label powerLabel1) {
        this.powerLabel1 = powerLabel1;
    }


    public Tile getDataGauge1() {
        return dataGauge1;
    }

    public void setDataGauge1(Tile dataGauge1) {
        this.dataGauge1 = dataGauge1;
    }


    @Override
    public void invalidated(Observable observable) {
        //dataTile1.setValue(((ISensor) observable).getValue());
        //ChartData c = new ChartData();
        //c.setTimestamp(((BasicSensor) observable).getTimestamp());
        //c.setValue(((BasicSensor) observable).getValue());
        //c.setTimestamp(Instant.now());
        //c.setValue((new Random().nextDouble())*1000);
        //Platform.runLater(new Thread(() -> dataTile1.addChartData(c)));

//        try{
//            var sensor = ((Sensor) observable);
//
//            //Platform.runLater(new Thread(() -> sensor.getDestination().addChartData((ChartData) sensor)));
//        } catch (Exception e){
//
//            try{
//                var sensor = ((ISensor) observable);
//                Platform.runLater(new Thread(() -> root.rotateByX((int) Math.round((((GyroSensor) observable).getValueGyro()[0])/10)*10)));
//                Platform.runLater(new Thread(() -> root.rotateByY((int) Math.round((((GyroSensor) observable).getValueGyro()[1])/10)*10)));
//                Platform.runLater(new Thread(() -> root.rotateByZ((int) Math.round((((GyroSensor) observable).getValueGyro()[2])/10)*10)));
//
//            } catch (Exception e2){
//
//            }
//        }


        Platform.runLater(new Thread(){
            @Override
            public void run() {
                dataIndicator1.setOn(new Random().nextBoolean());
                LatLong pos = new LatLong((47.6597 + new Random().nextDouble()/100.0), (-122.3357+new Random().nextDouble()/100.0));
                MarkerOptions markerOptions1 = new MarkerOptions();
                markerOptions1.position(pos);
                Marker marker = new Marker(markerOptions1);
                Platform.runLater(new Thread(() -> map.addMarker( marker )));
            }
        });

        //dataGauge1.setValue(((ISensor) observable).getValue());
        //dataGauge10.setValue(((ISensor) observable).getValue());


        //System.out.println(dataTile1.getId());
        //System.out.println(dataGauge1.);
        //dataTile1.addChartData((ChartData) observable);

    }


    @Override
    public void mapInitialized() {
        LatLong joeSmithLocation = new LatLong(47.6197, -122.3231);
        LatLong joshAndersonLocation = new LatLong(47.6297, -122.3431);
        LatLong bobUnderwoodLocation = new LatLong(47.6397, -122.3031);
        LatLong tomChoiceLocation = new LatLong(47.6497, -122.3325);
        LatLong fredWilkieLocation = new LatLong(47.6597, -122.3357);


        //Set the initial properties of the map.
        MapOptions mapOptions = new MapOptions();

        mapOptions.center(new LatLong(47.6097, -122.3331))
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .zoom(12);

        map = mapView.createMap(mapOptions);

        //Add markers to the map
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(joeSmithLocation);

        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(joshAndersonLocation);

        MarkerOptions markerOptions3 = new MarkerOptions();
        markerOptions3.position(bobUnderwoodLocation);

        MarkerOptions markerOptions4 = new MarkerOptions();
        markerOptions4.position(tomChoiceLocation);

        MarkerOptions markerOptions5 = new MarkerOptions();
        markerOptions5.position(fredWilkieLocation);

        Marker joeSmithMarker = new Marker(markerOptions1);
        Marker joshAndersonMarker = new Marker(markerOptions2);
        Marker bobUnderwoodMarker = new Marker(markerOptions3);
        Marker tomChoiceMarker= new Marker(markerOptions4);
        Marker fredWilkieMarker = new Marker(markerOptions5);

        map.addMarker( joeSmithMarker );
        map.addMarker( joshAndersonMarker );
        map.addMarker( bobUnderwoodMarker );
        map.addMarker( tomChoiceMarker );
        map.addMarker( fredWilkieMarker );

        InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
        infoWindowOptions.content("<h2>Fred Wilkie</h2>"
                + "Current Location: Safeway<br>"
                + "ETA: 45 minutes" );

        InfoWindow fredWilkeInfoWindow = new InfoWindow(infoWindowOptions);
        fredWilkeInfoWindow.open(map, fredWilkieMarker);
    }
    class SmartGroup extends Group {

      //  Rotate r;
       // Transform t = new Rotate();
        private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);

        private double oldX = 0;
        private double oldY = 0;
        private double oldZ = 0;

        public SmartGroup(){
            this.getTransforms().addAll(rotateX, rotateY, rotateZ);
        }

        void rotateByX(int ang) {
           /* r = new Rotate(ang, Rotate.X_AXIS);
            t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);*/
            rotateX.setAngle(ang);
        }

        void rotateByY(int ang) {
           /* r = new Rotate(ang, Rotate.Y_AXIS);
            t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);
            */rotateY.setAngle(ang);

        }
        void rotateByZ(int ang) {
           /* r = new Rotate(ang, Rotate.Z_AXIS);
            t = t.createConcatenation(r);
            this.getTransforms().clear();
            this.getTransforms().addAll(t);
            */rotateZ.setAngle(ang);

        }
    }

}

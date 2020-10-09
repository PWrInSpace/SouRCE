package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.interactivemesh.jfx.importer.ModelImporter;
import com.interactivemesh.jfx.importer.tds.TdsModelImporter;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.tilesfx.addons.Indicator;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SubScene;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.GyroSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.IGyroSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;

public class MainControllerDAS implements InvalidationListener {

    private ControllerNameEnum controllerNameEnum = ControllerNameEnum.MAIN_CONTROLLER;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

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
    private TextArea inComing;

    @FXML
    private TextArea outGoing;

    @FXML
    private SubScene modelScene;

    @FXML
    private SubScene dataScene;

    @FXML
    private SubScene valvesScene;

    @FXML
    private SubScene mapScene;

    @FXML
    private SubScene powerScene;


    public HashMap<Object, Gauge> sensors = new HashMap<Object, Gauge>();

    private MainControllerDAS.SmartGroup root = new MainControllerDAS.SmartGroup();
    private Node rocket3DModel;

    public SubScene getMapScene() {
        return mapScene;
    }

    public void initSubcenes(FXMLLoader loaderData, FXMLLoader loaderMap, FXMLLoader loaderPower, FXMLLoader loaderValves) {
        try {
            dataScene.setRoot(loaderData.load());
            mapScene.setRoot(loaderMap.load());
            powerScene.setRoot(loaderPower.load());
            valvesScene.setRoot(loaderValves.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setDataScene(SubScene dataScene) {
        this.dataScene = dataScene;
    }

    public SubScene getDataScene() {
        return dataScene;
    }

    @FXML
    void initialize() {
        assert dataGauge9 != null : "fx:id=\"dataGauge9\" was not injected: check your FXML file 'MainViewDAS.fxml'.";
        assert dataGauge10 != null : "fx:id=\"dataGauge10\" was not injected: check your FXML file 'MainViewDAS.fxml'.";
        assert dataIndicator1 != null : "fx:id=\"dataIndicator1\" was not injected: check your FXML file 'MainViewDAS.fxml'.";
        assert dataIndicator2 != null : "fx:id=\"dataIndicator2\" was not injected: check your FXML file 'MainViewDAS.fxml'.";
        assert dataIndicator4 != null : "fx:id=\"dataIndicator4\" was not injected: check your FXML file 'MainViewDAS.fxml'.";
        assert dataIndicator3 != null : "fx:id=\"dataIndicator3\" was not injected: check your FXML file 'MainViewDAS.fxml'.";
        assert inComing != null : "fx:id=\"inComing\" was not injected: check your FXML file 'MainViewDAS.fxml'.";
        assert outGoing != null : "fx:id=\"outGoing\" was not injected: check your FXML file 'MainViewDAS.fxml'.";
        assert modelScene != null : "fx:id=\"modelScene\" was not injected: check your FXML file 'MainViewDAS.fxml'.";
        assert dataScene != null : "fx:id=\"dataScene\" was not injected: check your FXML file 'MainViewDAS.fxml'.";
        assert valvesScene != null : "fx:id=\"valvesScene\" was not injected: check your FXML file 'MainViewDAS.fxml'.";
        assert mapScene != null : "fx:id=\"mapScene\" was not injected: check your FXML file 'MainViewDAS.fxml'.";
        assert powerScene != null : "fx:id=\"powerScene\" was not injected: check your FXML file 'MainViewDAS.fxml'.";


       /* dataTile1.setMaxTimePeriod(Duration.ofSeconds(15));
        dataTile1.setMaxValue(1000.0);
        //dataTile1.setStrokeWithGradient(true);
        dataTile1.setSmoothing(true);
*/
        //ChartData c = new ChartData();
        //dataTile1.addChartData(c);

        //Creating camera - i don`t know why but this is in exmaple
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-700);
        camera.setNearClip(0.1);
        camera.setFarClip(3000.0);
        camera.setFieldOfView(60);
        modelScene.setCamera(camera);


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
        modelScene.setFill(Color.SKYBLUE);

        //importing 3ds model
        ModelImporter tdsImporter = new TdsModelImporter();
        tdsImporter.read(getClass().getClassLoader().getResource("rocketModel.3ds"));
        Node[] tdsMesh = (Node[]) tdsImporter.getImport();

        rocket3DModel = tdsMesh[0];
        tdsImporter.close();
        root.getChildren().add(rocket3DModel);
        modelScene.setRoot(root);

//        //load dataScene
//        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("DataController.fxml"));
//        try {
//            dataScene.setRoot(loader.load());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        //end
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
//            var sensor = ((BasicSensor) observable);
//            Platform.runLater(new Thread(() -> sensor.getDestination().addChartData((ChartData) sensor)));
//        } catch (Exception e){
//
//            try{
//                var sensor = ((GyroSensor) observable);
//                Platform.runLater(new Thread(() -> root.rotateByX((int) Math.round((((GyroSensor) observable).getValue()[0])/10)*10)));
//                Platform.runLater(new Thread(() -> root.rotateByY((int) Math.round((((GyroSensor) observable).getValue()[1])/10)*10)));
//                Platform.runLater(new Thread(() -> root.rotateByZ((int) Math.round((((GyroSensor) observable).getValue()[2])/10)*10)));
//
//            } catch (Exception e2){
//
//            }
//        }
        if(observable instanceof MessageParser){
            Platform.runLater(new Thread( () -> inComing.appendText(((MessageParser) observable).getLastMessage())));
        }

//        dataGauge9.setValue(Math.pow(((((GyroSensor) observable).getValueGyro()[0])/10)*10,2)/1000);
//        dataGauge10.setValue(Math.pow(((((GyroSensor) observable).getValueGyro()[1])/10)*10,2)/1000);
//        /*if(observable instanceof SerialPortManager){
//            ((SerialPortManager) observable)
//        }*/
//
//
//         else if(observable instanceof GyroSensor){
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
        else if(observable instanceof IGyroSensor){
            Platform.runLater(new Thread(() -> root.rotateByX((int) Math.round((((IGyroSensor) observable).getValueGyro().get(IGyroSensor.AXIS_X_KEY))/10)*10)));
            Platform.runLater(new Thread(() -> root.rotateByY((int) Math.round((((IGyroSensor) observable).getValueGyro().get(IGyroSensor.AXIS_Y_KEY))/10)*10)));
            Platform.runLater(new Thread(() -> root.rotateByZ((int) Math.round((((IGyroSensor) observable).getValueGyro().get(IGyroSensor.AXIS_Z_KEY))/10)*10)));
        }



        //dataGauge1.setValue(((ISensor) observable).getValue());
        //dataGauge10.setValue(((ISensor) observable).getValue());


        //System.out.println(dataTile1.getId());
        //System.out.println(dataGauge1.);
        //dataTile1.addChartData((ChartData) observable);

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

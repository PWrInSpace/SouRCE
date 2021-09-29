package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.interactivemesh.jfx.importer.ModelImporter;
import com.interactivemesh.jfx.importer.tds.TdsModelImporter;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.IMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.IGyroSensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.ISerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class MainController extends BasicController implements InvalidationListener {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    private static final double initWidth = 1550.4;
    private static final double initHeight = 838.4;

    @FXML
    private TextArea inComing;

    @FXML
    private SubScene dataScene;

    @FXML
    private SubScene mapScene;

    @FXML
    private SubScene powerScene;

    @FXML
    private SubScene abortScene;

    @FXML
    private AnchorPane footer;

    @FXML
    private ImageView poliwrocketLogo;

    @FXML
    private ImageView inSpaceLogo;

    @FXML
    private SubScene rawDataScene;

    @FXML
    private SubScene connectionScene;

    @FXML
    private TabPane tabPane;

    @FXML
    private TextArea outGoing;

    @FXML
    private SubScene dataSceneFilling;

    @FXML
    private SubScene valvesScene;

    @FXML
    private SubScene dataSceneFlight;

    @FXML
    private SubScene modelScene;

    @FXML
    private SubScene moreDataScene;

    @FXML
    private SubScene indicatorsScene;

    @FXML
    private SubScene startControlScene;

    @FXML
    private SubScene settingsScene;

    private final MainController.SmartGroup root = new SmartGroup();

    private Stage primaryStage;

    private final List<Node> nodes = new ArrayList<>();
    private final HashMap<Node,Pair<Double,Double>> nodesInitPositions = new HashMap<>();

    public SubScene getMapScene() {
        return mapScene;
    }

    public void initSubScenes(FXMLLoader loaderData, FXMLLoader loaderMap, FXMLLoader loaderPower,
                              FXMLLoader loaderValves, FXMLLoader loaderMoreData, FXMLLoader loaderAbort,
                              FXMLLoader loaderIndicators, FXMLLoader loaderStart, FXMLLoader loaderConnection,
                              FXMLLoader loaderRawData, FXMLLoader loaderFilling, FXMLLoader loaderFlight,
                              FXMLLoader loaderSettings) {
        try {
            dataScene.setRoot(loaderData.load());
            mapScene.setRoot(loaderMap.load());
            powerScene.setRoot(loaderPower.load());
            valvesScene.setRoot(loaderValves.load());
            moreDataScene.setRoot(loaderMoreData.load());
            abortScene.setRoot(loaderAbort.load());
            indicatorsScene.setRoot(loaderIndicators.load());
            startControlScene.setRoot(loaderStart.load());
            connectionScene.setRoot(loaderConnection.load());
            rawDataScene.setRoot(loaderRawData.load());
            dataSceneFilling.setRoot(loaderFilling.load());
            dataSceneFlight.setRoot(loaderFlight.load());
            settingsScene.setRoot(loaderSettings.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.MAIN_CONTROLLER;

        modelScene.setVisible(false);

        //add nodes to list
        nodes.add(dataScene);
        nodes.add(mapScene);
        nodes.add(powerScene);
        nodes.add(abortScene);
        nodes.add(connectionScene);
        nodes.add(modelScene);
        nodes.add(footer);
        nodes.add(rawDataScene);
        nodes.add(indicatorsScene);
        nodes.add(outGoing);
        nodes.add(inComing);
        nodes.add(tabPane);
        nodes.forEach(scene -> nodesInitPositions.put(scene,new Pair<>(scene.getLayoutX(),scene.getLayoutY())));

        //set logo
        poliwrocketLogo.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("Poliwrocket.png"))));
        inSpaceLogo.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("inSpaceLogo.png"))));


        //Creating camera
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-125); //-900
        camera.setNearClip(0.01);
        camera.setFarClip(3000.0);
        camera.setFieldOfView(60);
        modelScene.setCamera(camera);


        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(0);
        light.setTranslateY(6000);
        light.setTranslateZ(300);
        root.getChildren().add(light);

        AmbientLight ambiance = new AmbientLight(Color.LIGHTGREY);
        root.getChildren().add(ambiance);

        //importing 3ds model
        ModelImporter tdsImporter = new TdsModelImporter();
        try {
            tdsImporter.read("./assets/rocketModel/rocketModel.3DS");
        } catch (Exception e){
            logger.error(e.getMessage());
            logger.info("Loading default model.");
            tdsImporter.read(getClass().getClassLoader().getResource("rocketModel.3DS"));
        }
        Node[] tdsMesh = (Node[]) tdsImporter.getImport();

        Node rocket3DModel = tdsMesh[0];
        tdsImporter.close();
        root.getChildren().add(rocket3DModel);
        modelScene.setRoot(root);

        modelScene.setOnScroll(scrollEvent -> modelScene.getCamera().setTranslateZ(Double.min(0 , modelScene.getCamera().getTranslateZ() + scrollEvent.getDeltaY())));
    }

    @Override
    public void invalidated(Observable observable) {
        if (observable instanceof IMessageParser) {
            var value = ((IMessageParser) observable).getLastMessage();
            if(!value.contains("\n")) {
                value += "\n";
            }
            String currentLog = inComing.getText();
            String[] currentLogs = currentLog.split("\n");
            if(currentLogs.length > 30)
                currentLog = Arrays.stream(currentLogs).skip(5).collect(Collectors.joining("\n"));
            currentLog += value;
            String finalValue = currentLog;
            UIThreadManager.getInstance().addImmediate(() -> {
                double pos = inComing.getScrollTop();
                int anchor = inComing.getAnchor();
                int caret = inComing.getCaretPosition();
                inComing.clear();
                inComing.appendText(finalValue);
                inComing.setScrollTop(pos);
                inComing.selectRange(anchor, caret);
            });
        } else if (observable instanceof IGyroSensor) {
            var sensorValues = ((IGyroSensor) observable).getValueGyro();
            UIThreadManager.getInstance().addImmediateOnOK(() -> {
                root.rotateByX((int) Math.round((sensorValues.get(IGyroSensor.AXIS_X_KEY)) / 10) * 10);
                root.rotateByY((int) Math.round((sensorValues.get(IGyroSensor.AXIS_Y_KEY)) / 10) * 10);
                root.rotateByZ((int) Math.round((sensorValues.get(IGyroSensor.AXIS_Z_KEY)) / 10) * 10);
            });
        } else if (observable instanceof ISerialPortManager) {
            var value = ((ISerialPortManager) observable).getLastSend() + "\n";
            Platform.runLater(() -> outGoing.appendText(value));
            Platform.requestNextPulse();
        } else if(primaryStage.heightProperty().equals(observable) || primaryStage.widthProperty().equals(observable)) {
            scaleSubScenes(primaryStage.widthProperty().doubleValue()/initWidth,primaryStage.heightProperty().doubleValue()/initHeight);
        }
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    private void scaleSubScenes(double scaleX, double scaleY) {
        nodes.forEach(scene -> {
            if(!scene.getTransforms().isEmpty()) {
                scene.getTransforms().clear();
            }

            scene.getTransforms().add(new Scale(scaleX,scaleY));
            scene.setLayoutX(nodesInitPositions.get(scene).getValue0() * scaleX);
            scene.setLayoutY(nodesInitPositions.get(scene).getValue1() * scaleY);
        });
    }


    static class SmartGroup extends Group {

        private final Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
        private final Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
        private final Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);

        public SmartGroup() {
            this.getTransforms().addAll(rotateX, rotateY, rotateZ);
        }

        void rotateByX(int ang) {
            rotateX.setAngle(ang);
        }

        void rotateByY(int ang) {
            rotateY.setAngle(ang);
        }

        void rotateByZ(int ang) {
            rotateZ.setAngle(ang);
        }
    }
}

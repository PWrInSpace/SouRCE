package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.interactivemesh.jfx.importer.ModelImporter;
import com.interactivemesh.jfx.importer.tds.TdsModelImporter;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.IGyroSensor;

import java.io.IOException;
import java.util.Objects;

public class MainController extends BasicController implements InvalidationListener {

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    @FXML
    private SubScene modelScene;

    @FXML
    private SubScene dataScene;

    @FXML
    private SubScene valvesScene;

    @FXML
    private SubScene moreDataScene;

    @FXML
    private SubScene stateScene;

    @FXML
    private SubScene startControlScene;

    @FXML
    private SubScene connectionScene;

    @FXML
    private SubScene abortScene;

    @FXML
    private SubScene mapScene;

    @FXML
    private SubScene powerScene;

    @FXML
    private TextArea inComing;

    @FXML
    private TextArea outGoing;

    @FXML
    private ImageView poliwrocketLogo;

    @FXML
    private ImageView inSpaceLogo;

    private final MainController.SmartGroup root = new SmartGroup();

    public SubScene getMapScene() {
        return mapScene;
    }

    public void initSubscenes(FXMLLoader loaderData, FXMLLoader loaderMap, FXMLLoader loaderPower,
                              FXMLLoader loaderValves, FXMLLoader loaderMoreData, FXMLLoader loaderAbort,
                              FXMLLoader loaderStates, FXMLLoader loaderStart, FXMLLoader loaderConnection) {
        try {
            dataScene.setRoot(loaderData.load());
            mapScene.setRoot(loaderMap.load());
            powerScene.setRoot(loaderPower.load());
            valvesScene.setRoot(loaderValves.load());
            moreDataScene.setRoot(loaderMoreData.load());
            abortScene.setRoot(loaderAbort.load());
            stateScene.setRoot(loaderStates.load());
            startControlScene.setRoot(loaderStart.load());
            connectionScene.setRoot(loaderConnection.load());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        controllerNameEnum = ControllerNameEnum.MAIN_CONTROLLER;

        assert inComing != null : "fx:id=\"inComing\" was not injected: check your FXML file 'MainView.fxml'.";
        assert outGoing != null : "fx:id=\"outGoing\" was not injected: check your FXML file 'MainView.fxml'.";
        assert modelScene != null : "fx:id=\"modelScene\" was not injected: check your FXML file 'MainView.fxml'.";
        assert dataScene != null : "fx:id=\"dataScene\" was not injected: check your FXML file 'MainView.fxml'.";
        assert valvesScene != null : "fx:id=\"valvesScene\" was not injected: check your FXML file 'MainView.fxml'.";
        assert mapScene != null : "fx:id=\"mapScene\" was not injected: check your FXML file 'MainView.fxml'.";
        assert powerScene != null : "fx:id=\"powerScene\" was not injected: check your FXML file 'MainView.fxml'.";
        //set logo
        poliwrocketLogo.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("Poliwrocket.png"))));
        inSpaceLogo.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("inSpaceLogo.png"))));


        //Creating camera - i don`t know why but this is in example
        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-125); //-900
        camera.setNearClip(0.01);
        camera.setFarClip(3000.0);
        camera.setFieldOfView(60);
        modelScene.setCamera(camera);


        //nie usuwac, dziala, pozniej zdecyduje czy lepiej z czy bez

        PointLight light = new PointLight(Color.WHITE);
        light.setTranslateX(0);
        light.setTranslateY(6000);
        light.setTranslateZ(300);
        root.getChildren().add(light);

        AmbientLight ambiance = new AmbientLight(Color.LIGHTGREY);
        root.getChildren().add(ambiance);


        //setting background color
//        modelScene.setFill(Color.DEEPSKYBLUE);

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
        if (observable instanceof MessageParser) {
            Platform.runLater(() -> inComing.appendText(((MessageParser) observable).getLastMessage()));
        }
        else if (observable instanceof IGyroSensor) {
            Platform.runLater(() -> root.rotateByX((int) Math.round((((IGyroSensor) observable).getValueGyro().get(IGyroSensor.AXIS_X_KEY)) / 10) * 10));
            Platform.runLater(() -> root.rotateByY((int) Math.round((((IGyroSensor) observable).getValueGyro().get(IGyroSensor.AXIS_Y_KEY)) / 10) * 10));
            Platform.runLater(() -> root.rotateByZ((int) Math.round((((IGyroSensor) observable).getValueGyro().get(IGyroSensor.AXIS_Z_KEY)) / 10) * 10));
        }
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

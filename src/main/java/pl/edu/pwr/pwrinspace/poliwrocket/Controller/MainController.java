package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.interactivemesh.jfx.importer.ModelImporter;
import com.interactivemesh.jfx.importer.tds.TdsModelImporter;
import com.jfoenix.controls.JFXTextArea;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.*;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;
import org.javatuples.Pair;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.IMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.ISerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.UI.UIThreadManager;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class MainController extends BasicController implements InvalidationListener {

    private static final double initWidth = 1550.4;
    private static final double initHeight = 838.4;

    @FXML
    private SubScene liquidDataScene;
    @FXML
    private SubScene liquidIndicatorsScene;
    @FXML
    private SubScene launchScene;
    @FXML
    private SubScene liquidCommandsScene;
    @FXML
    private SubScene liquidCommands2Scene;
    @FXML
    private JFXTextArea inComing;
    @FXML
    private SubScene dataScene;
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
    private SubScene otherScene;
    @FXML
    private TabPane tabPane;
    @FXML
    private JFXTextArea outGoing;
    @FXML
    private SubScene rocketSettingsScene;
    @FXML
    private SubScene armingCommandsScene;

    private final SmartGroup root = new SmartGroup();

    private Stage primaryStage;

    private final List<Node> nodes = new ArrayList<>();
    private final HashMap<Node,Pair<Double,Double>> nodesInitPositions = new HashMap<>();


    public void initSubScenes(Collection<FXMLLoader> fxmlLoaders) {
        try {
            HashMap<String, Field> fields = new HashMap<>();

            for (Field declaredField : this.getClass().getDeclaredFields()) {
                String fieldName = declaredField.getName();
                if(fieldName.endsWith("Scene"))
                    fields.put(fieldName.replace("Scene","").toLowerCase(),declaredField);
            }

            for (FXMLLoader fxmlLoader : fxmlLoaders) {
                Parent loaded = fxmlLoader.load();
                String className = fxmlLoader.getController().getClass().getSimpleName().replace("Controller", "").toLowerCase();
                var field = fields.get(className);
                if(field != null) {
                    ((SubScene)field.get(this)).setRoot(loaded);
                } else {
                    logger.error(String.format("Scene not found for %s", className));
                }
            }
        } catch (IOException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void initialize() {
        addNodesForAppScalingPurpose();
        setAppImages();
    }

    private void setAppImages() {
        //set logo
        poliwrocketLogo.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("Poliwrocket.png"))));
        inSpaceLogo.setImage(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("inSpaceLogo.png"))));
    }

    private void addNodesForAppScalingPurpose() {
        //add nodes to list, ONLY nodes that are directly on main panel, it is necessary for scaling app window
        nodes.add(dataScene);
        nodes.add(powerScene);
        nodes.add(abortScene);
        nodes.add(footer);
        nodes.add(rawDataScene);
        nodes.add(outGoing);
        nodes.add(inComing);
        nodes.add(tabPane);
        nodes.add(liquidIndicatorsScene);
        nodes.add(armingCommandsScene);
        nodes.forEach(scene -> nodesInitPositions.put(scene,new Pair<>(scene.getLayoutX(),scene.getLayoutY())));
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
        } else if (observable instanceof ISerialPortManager) {
            var value = ((ISerialPortManager) observable).getLastSend() + "\n";
            Platform.runLater(() -> outGoing.appendText(value));
            Platform.requestNextPulse();
        } else if (observable == Configuration.getInstance()) {
            if(Configuration.getInstance().isForceCommandsActive()) {
                outGoing.setStyle("-fx-border-color: red;");
            } else {
                outGoing.setStyle("");
            }
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

}

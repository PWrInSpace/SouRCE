package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.TabPane;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

public class DetachedTabController implements InvalidationListener {

    @FXML
    private TabPane detachedTabPane;

    private Stage myStage;

    private static final double initWidth = 1550.4;
    private static final double initHeight = 838.4;

    public TabPane getTabPane() {
        return this.detachedTabPane;
    }

    public void setStage(Stage stage) {
        this.myStage = stage;

        stage.widthProperty().addListener(this);
        stage.heightProperty().addListener(this);
    }

    @Override
    public void invalidated(Observable observable) {
        if (myStage.widthProperty().equals(observable) || myStage.heightProperty().equals(observable)) {
            scaleContent(myStage.getWidth() / initWidth, myStage.getHeight() / initHeight);
        }
    }

    private void scaleContent(double scaleX, double scaleY) {
        if (detachedTabPane != null) {
            if(!detachedTabPane.getTransforms().isEmpty()) {
                detachedTabPane.getTransforms().clear();
            }
            detachedTabPane.getTransforms().add(new Scale(scaleX, scaleY, 0, 0));
        }
    }

}

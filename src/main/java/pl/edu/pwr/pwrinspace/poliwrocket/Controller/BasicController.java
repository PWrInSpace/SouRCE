package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import javafx.beans.InvalidationListener;
import javafx.fxml.FXML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasicController implements InvalidationListener {

    protected static final Logger logger = LoggerFactory.getLogger(BasicController.class);

    public String getControllerName() {
        return getClass().getSimpleName().replace("Controller", "");
    }

    @FXML
    protected void initialize() {};
}

package pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController;

import javafx.beans.InvalidationListener;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.ControllerNameEnum;

public abstract class BasicController implements InvalidationListener {
    protected ControllerNameEnum controllerNameEnum;

    public ControllerNameEnum getControllerNameEnum() {
        return this.controllerNameEnum;
    }
}

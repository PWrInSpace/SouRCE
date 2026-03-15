package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import javafx.beans.Observable;

public abstract class BaseNewComponentController extends BaseController {
    BaseController parentController;

    @Override
    public void invalidated(Observable observable) {}

    public void setParentController(BaseController parentController) {
        this.parentController = parentController;
    }
}

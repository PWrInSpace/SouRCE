package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import javafx.scene.Group;
import javafx.scene.transform.Rotate;

public class SmartGroup extends Group {

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
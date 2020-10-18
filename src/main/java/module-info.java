
module pl.edu.pwr.pwrinspace.poliwrocket {
    requires com.sothawo.mapjfx;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires org.slf4j;
    requires eu.hansolo.tilesfx;
    requires eu.hansolo.medusa;
    requires GMapsFX;
    requires importer;
    requires annotations;
    requires java.logging;
    requires io;
    requires com.google.gson;
    requires javatuples;
    requires net.dv8tion.jda;
    requires com.fasterxml.jackson.core;
    requires java.desktop;


    opens pl.edu.pwr.pwrinspace.poliwrocket to javafx.fxml, javafx.controls, javafx.web, javafx.graphics, javafx.media, javafx.base, com.google.gson;
    opens pl.edu.pwr.pwrinspace.poliwrocket.Controller to javafx.fxml, javafx.controls, javafx.web, javafx.graphics, javafx.media, javafx.base;
    opens pl.edu.pwr.pwrinspace.poliwrocket.Model to com.google.gson;
    opens pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor to com.google.gson;

    exports pl.edu.pwr.pwrinspace.poliwrocket.Controller to javafx.fxml, javafx.controls, javafx.web, javafx.graphics, javafx.media, javafx.base;
    exports pl.edu.pwr.pwrinspace.poliwrocket.Model;
    exports pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor;
    exports pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification;
}

package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.sothawo.mapjfx.*;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.IGPSSensor;

import java.util.ArrayList;
import java.util.List;

public class NewMapController extends BasicController implements InvalidationListener {

    private ControllerNameEnum controllerNameEnum = ControllerNameEnum.MAP_CONTROLLER;

    /** logger for the class. */
    private static final Logger logger = LoggerFactory.getLogger(NewMapController.class);

    /** default zoom value. */
    private static final int ZOOM_DEFAULT = 60;

    /** the MapView containing the map */
    @FXML
    private MapView mapView;

    @FXML
    public TextField currentLocation;

    /** Coordinateline for rocket tracking. */
    private CoordinateLine track;

    public NewMapController() {

    }

    /**
     * called after the fxml is loaded and all objects are created. This is not called initialize any more,
     * because we need to pass in the projection before initializing.
     *
     * @param projection
     *     the projection to use in the map.
     */
    public void initMapAndControls(Projection projection) {
        logger.trace("begin initialize");

//        // init MapView-Cache
//        final OfflineCache offlineCache = mapView.getOfflineCache();
//        final String cacheDir = System.getProperty("java.io.tmpdir") + "/mapjfx-cache";
//        logger.info("using dir for cache: " + cacheDir);
//        try {
//            Files.createDirectories(Paths.get(cacheDir));
//            offlineCache.setCacheDirectory(cacheDir);
//            offlineCache.setActive(true);
//        } catch (IOException e) {
//            logger.warn("could not activate offline cache", e);
//        }

        // set the custom css file for the MapView
        mapView.setCustomMapviewCssURL(getClass().getResource("/custom_mapview.css"));

        // watch the MapView's initialized property to finish initialization
        mapView.initializedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                afterMapIsInitialized();
            }
        });

        mapView.setMapType(MapType.OSM);

        // finally initialize the map view
        logger.trace("start map initialization");
        mapView.initialize(Configuration.builder()
                .projection(projection)
                .showZoomControls(false)
                .build());
        logger.debug("initialization finished");
    }

    /**
     * finishes setup after the mpa is initialzed
     */
    private void afterMapIsInitialized() {
        logger.trace("map intialized");
        logger.debug("setting center and zoom...");

        mapView.setZoom(ZOOM_DEFAULT);
        Coordinate startPosition = new Coordinate(pl.edu.pwr.pwrinspace.poliwrocket.Configuration.getInstance().START_POSITION_LAT, pl.edu.pwr.pwrinspace.poliwrocket.Configuration.getInstance().START_POSITION_LON);
        mapView.setCenter(startPosition);
    }

    @Override
    public void invalidated(Observable observable) {
        Platform.runLater(() -> {
            Coordinate nextCoordinate = new Coordinate(((IGPSSensor) observable).getPosition().get(IGPSSensor.LATITUDE_KEY) , ((IGPSSensor) observable).getPosition().get(IGPSSensor.LONGITUDE_KEY));
            currentLocation.setText(nextCoordinate.getLatitude()+";"+nextCoordinate.getLongitude());
            final List<Coordinate> coordinates = new ArrayList<>();
            if (track != null) {
                track.getCoordinateStream().forEach(coordinates::add);
                mapView.removeCoordinateLine(track);
                track = null;
            }
            coordinates.add(nextCoordinate);
            track = new CoordinateLine(coordinates)
                    .setColor(Color.DODGERBLUE)
                    .setWidth(7)
                    .setClosed(false);
            mapView.addCoordinateLine(track);
            track.setVisible(true);
        });
    }

    @Override
    public ControllerNameEnum getControllerNameEnum() {
        return this.controllerNameEnum;
    }
}

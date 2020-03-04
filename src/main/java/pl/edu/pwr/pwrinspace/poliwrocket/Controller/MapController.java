package pl.edu.pwr.pwrinspace.poliwrocket.Controller;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.IGPSSensor;

public class MapController implements InvalidationListener, MapComponentInitializedListener {

    @FXML
    private GoogleMapView mapView;

    private GoogleMap map;

    @FXML
    void initialize() {
        assert mapView != null : "fx:id=\"mapView\" was not injected: check your FXML file 'MapView.fxml'.";
        mapView.addMapInializedListener(this);
    }

    @Override
    public void mapInitialized() {
        LatLong joeSmithLocation = new LatLong(47.6197, -122.3231);
        LatLong joshAndersonLocation = new LatLong(47.6297, -122.3431);
        LatLong bobUnderwoodLocation = new LatLong(47.6397, -122.3031);
        LatLong tomChoiceLocation = new LatLong(47.6497, -122.3325);
        LatLong fredWilkieLocation = new LatLong(47.6597, -122.3357);


        //Set the initial properties of the map.
        MapOptions mapOptions = new MapOptions();

        mapOptions.center(new LatLong(47.6097, -122.3331))
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .zoom(12);

        map = mapView.createMap(mapOptions);

        //Add markers to the map
        MarkerOptions markerOptions1 = new MarkerOptions();
        markerOptions1.position(joeSmithLocation);

        MarkerOptions markerOptions2 = new MarkerOptions();
        markerOptions2.position(joshAndersonLocation);

        MarkerOptions markerOptions3 = new MarkerOptions();
        markerOptions3.position(bobUnderwoodLocation);

        MarkerOptions markerOptions4 = new MarkerOptions();
        markerOptions4.position(tomChoiceLocation);

        MarkerOptions markerOptions5 = new MarkerOptions();
        markerOptions5.position(fredWilkieLocation);

        Marker joeSmithMarker = new Marker(markerOptions1);
        Marker joshAndersonMarker = new Marker(markerOptions2);
        Marker bobUnderwoodMarker = new Marker(markerOptions3);
        Marker tomChoiceMarker= new Marker(markerOptions4);
        Marker fredWilkieMarker = new Marker(markerOptions5);

        map.addMarker( joeSmithMarker );
        map.addMarker( joshAndersonMarker );
        map.addMarker( bobUnderwoodMarker );
        map.addMarker( tomChoiceMarker );
        map.addMarker( fredWilkieMarker );

        InfoWindowOptions infoWindowOptions = new InfoWindowOptions();
        infoWindowOptions.content("<h2>Fred Wilkie</h2>"
                + "Current Location: Safeway<br>"
                + "ETA: 45 minutes" );

        InfoWindow fredWilkeInfoWindow = new InfoWindow(infoWindowOptions);
        fredWilkeInfoWindow.open(map, fredWilkieMarker);
    }

    @Override
    public void invalidated(Observable observable) {
        Platform.runLater(new Thread(){
            @Override
            public void run() {
                System.err.println(((IGPSSensor) observable).getPosition()[0]  + " " + ((IGPSSensor) observable).getPosition()[1]);
                LatLong pos = new LatLong(((IGPSSensor) observable).getPosition()[0], ((IGPSSensor) observable).getPosition()[1]);
                MarkerOptions markerOptions1 = new MarkerOptions();
                markerOptions1.position(pos);
                Marker marker = new Marker(markerOptions1);
                Platform.runLater(new Thread(() -> map.addMarker( marker )));
            }
        });
    }

}


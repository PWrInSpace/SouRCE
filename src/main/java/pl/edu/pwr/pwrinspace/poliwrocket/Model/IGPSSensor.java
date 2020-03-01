package pl.edu.pwr.pwrinspace.poliwrocket.Model;

public interface IGPSSensor {
     void setPosition(double latitude, double longitude);
     double[] getPosition();
     int getDataNumber();
}

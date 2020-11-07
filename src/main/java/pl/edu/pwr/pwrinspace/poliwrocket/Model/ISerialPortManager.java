package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import javafx.beans.Observable;

public interface ISerialPortManager extends Observable {
    void initialize();
    void close();
    void initialize(String portName, int dataRate);
    boolean isPortOpen();
    void write(String message);
}

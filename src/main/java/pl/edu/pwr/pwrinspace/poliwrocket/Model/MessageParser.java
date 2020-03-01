package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

public class MessageParser implements IMessageParser, Observable {

    private static MessageParser messageParser;

    private ISensorRepository sensorRepository;

    private IGPSSensor gpsSensor;

    private int gpsDataNumber = 0;

    private String lastMessage;

    private String delims = ",";

    private InvalidationListener observer;

    public static MessageParser getInstance() {
        return messageParser;
    }

    public static boolean create(ISensorRepository sensorRepository){
        if (messageParser == null){
            messageParser = new MessageParser(sensorRepository, null);
            return true;
        }
        return false;
    }
    public static boolean create(ISensorRepository sensorRepository, IGPSSensor gpsSensor){
        if (messageParser == null){
            messageParser = new MessageParser(sensorRepository, gpsSensor);
            return true;
        }
        return false;
    }

    private MessageParser(ISensorRepository sensorRepository, IGPSSensor gpsSensor) {
        this.sensorRepository = sensorRepository;
        this.gpsSensor = gpsSensor;
        if(gpsSensor!=null){
            gpsDataNumber = gpsSensor.getDataNumber();
        }
    }

    @Override
    public void parseMessage(byte[] msg, int length) {
        lastMessage = new String(msg,0,length);
        String[] splited = lastMessage.split(delims);
        if(sensorRepository.getSensorsKeys().size() + gpsDataNumber == splited.length){
            int currentPosition = 0;
            for (String name: sensorRepository.getSensorsKeys()) {
                try {
                    sensorRepository.getSensorByName(name).setValue(Double.parseDouble(splited[currentPosition]));
                } catch (NumberFormatException e){
                    System.err.println("Wrong message, value is not a number! " + lastMessage + " -> " + splited[currentPosition]);
                }
                currentPosition++;
            }
        } else {
            System.err.println("Wrong message length! Expected: " + sensorRepository.getSensorsKeys().size() + " got: " + splited.length);
        }
        parseGPS(splited);
        notifyObserver();
    }

    @Override
    public String getLastMessage() {
        return lastMessage;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        this.observer = invalidationListener;
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        if(observer != null){
            observer = null;
        }
    }

    private void notifyObserver(){
        if(observer!=null){
            observer.invalidated(this);
        }
    }

    private void parseGPS(String[] message){
        try {
           gpsSensor.setPosition(Double.parseDouble(message[message.length-gpsDataNumber]),Double.parseDouble(message[message.length-1]));
        } catch (NumberFormatException e){
            System.err.println("Wrong message, value is not a number! " + lastMessage + " -> GPS DATA");
        }
    }
}

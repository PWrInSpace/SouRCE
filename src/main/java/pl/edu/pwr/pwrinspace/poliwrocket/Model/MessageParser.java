package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MessageParser implements IMessageParser, Observable {

    private static MessageParser messageParser;

    private ISensorRepository sensorRepository;

    private String lastMessage;

    private String delims = ",";

    private InvalidationListener observer;

    public static MessageParser getInstance() {
        return messageParser;
    }

    public static boolean create(ISensorRepository sensorRepository){
        if (messageParser == null){
            messageParser = new MessageParser(sensorRepository);
            return true;
        }
        return false;
    }

    private MessageParser(ISensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    @Override
    public void parseMessage(byte[] msg, int length) {
        lastMessage = new String(msg,0,length);
        Logger.getLogger(getClass().getName()).log(Level.INFO,"Message received: " + lastMessage);
        String[] splited = lastMessage.split(delims);

        if(sensorRepository.getSensorsKeys().size() == splited.length){
            int currentPosition = 0;
            for (String name: sensorRepository.getSensorsKeys()) {
                try {
                    sensorRepository.getSensorByName(name).setValue(Double.parseDouble(splited[currentPosition]));
                } catch (NumberFormatException e){
                    Logger.getLogger(getClass().getName()).log(Level.WARNING,"Wrong message, value is not a number! " + lastMessage + " -> " + splited[currentPosition]);
                }
                currentPosition++;
            }
        } else {
            Logger.getLogger(getClass().getName()).log(Level.WARNING,"Wrong message length! Expected: " + sensorRepository.getSensorsKeys().size() + " got: " + splited.length);
        }
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
}

package pl.edu.pwr.pwrinspace.poliwrocket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.*;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.*;

import java.io.IOException;
import java.time.Instant;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Random;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{

        //FXMLLoader
        FXMLLoader loaderMain = new FXMLLoader(getClass().getClassLoader().getResource("MainView.fxml"));
        FXMLLoader loaderData = new FXMLLoader(getClass().getClassLoader().getResource("DataView.fxml"));
        FXMLLoader loaderMap = new FXMLLoader(getClass().getClassLoader().getResource("MapView.fxml"));
        FXMLLoader loaderPower = new FXMLLoader(getClass().getClassLoader().getResource("PowerView.fxml"));
        FXMLLoader loaderValves = new FXMLLoader(getClass().getClassLoader().getResource("ValvesView.fxml"));

        Scene scene = new Scene(loaderMain.load(), 1550, 750);

        MainController mainController = loaderMain.getController();
        mainController.initSubcenes(loaderData,loaderMap,loaderPower,loaderValves);

        DataController dataController = loaderData.getController();
        MapController mapController = loaderMap.getController();
        PowerController powerController = loaderPower.getController();
        ValvesController valvesController = loaderValves.getController();

        LinkedList<Sensor> sensorsData = new LinkedList<>();

        Sensor basicSensor = new Sensor();
        basicSensor.setDestination("dataGauge1");
        Sensor gryro1 = new Sensor();
        gryro1.setDestination("dataGauge2");
        gryro1.setName("Gyro X");
        Sensor gryro2 = new Sensor();
        gryro2.setDestination("dataGauge3");
        gryro2.setName("Gyro Y");
        Sensor gryro3 = new Sensor();
        gryro3.setDestination("dataGauge4");
        gryro3.setName("Gyro Z");
        sensorsData.add(basicSensor);
        sensorsData.add(gryro1);
        sensorsData.add(gryro2);
        sensorsData.add(gryro3);

        GyroSensor gyro =  new GyroSensor();
        GPSSensor gpsSensor = new GPSSensor();

        //dodanie listenerow do sensorow
        basicSensor.addListener(dataController);
        gryro1.addListener(dataController);
        gryro2.addListener(dataController);
        gryro3.addListener(dataController);
        gyro.addListener(mainController);
        gpsSensor.addListener(mapController);

        dataController.injectModel(sensorsData);

        primaryStage.setTitle("SouRCE");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        SensorRepository sensorRepository = new SensorRepository();

        sensorRepository.addSensor(gryro1);
        sensorRepository.addSensor(gryro2);
        sensorRepository.addSensor(gryro3);

        MessageParser.create(sensorRepository,gpsSensor);
        MessageParser.getInstance().addListener(mainController);
        SerialPortManager comm = SerialPortManager.getInstance();
        comm.gyro = gyro;
        SerialPortManager.getInstance().initialize();
        new Thread(() -> {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i=1; i<=32; i++){
                basicSensor.setValue((basicSensor.getValue()+10.0));
                gpsSensor.setPosition((47.6597 + new Random().nextDouble()/100.0), (-122.3357+new Random().nextDouble()/100.0));
                /*if(new Random().nextBoolean()){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop(){
        System.out.println("Stage is closing");
        SerialPortManager.getInstance().close();
        System.exit(-1);
    }
}

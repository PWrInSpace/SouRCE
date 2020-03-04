package pl.edu.pwr.pwrinspace.poliwrocket;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.*;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.*;

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
        sensorsData.add(basicSensor);

        //utworzenie 3xSensor for GYRO
        Sensor gryro1 = new Sensor();
        gryro1.setDestination("dataGauge2");
        gryro1.setName("Gyro X");
        Sensor gryro2 = new Sensor();
        gryro2.setDestination("dataGauge3");
        gryro2.setName("Gyro Y");
        Sensor gryro3 = new Sensor();
        gryro3.setDestination("dataGauge4");
        gryro3.setName("Gyro Z");
        sensorsData.add(gryro1);
        sensorsData.add(gryro2);
        sensorsData.add(gryro3);
        //--------

        //nowy gps
        Sensor latitude = new Sensor();
        latitude.setName("lat");
        Sensor longitude = new Sensor();
        longitude.setName("long");
        GPSSensor gpsSensor = new GPSSensor(latitude,longitude);
        latitude.addListener(gpsSensor);
        longitude.addListener(gpsSensor);
        gpsSensor.addListener(mapController);
        //--------

        //dodanie listenerow do sensorow
        basicSensor.addListener(dataController);
        gryro1.addListener(dataController);
        gryro2.addListener(dataController);
        gryro3.addListener(dataController);

        //nowy gryo
        GyroSensor gyroSensor = new GyroSensor(gryro1,gryro2,gryro3);
        gyroSensor.addListener(mainController);
        gryro1.addListener(gyroSensor);
        gryro2.addListener(gyroSensor);
        gryro3.addListener(gyroSensor);
        //---------

        dataController.injectModel(sensorsData);

        primaryStage.setTitle("SouRCE");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        SensorRepository sensorRepository = new SensorRepository();

        sensorRepository.addSensor(gryro1);
        sensorRepository.addSensor(gryro2);
        sensorRepository.addSensor(gryro3);

        MessageParser.create(sensorRepository);
        MessageParser.getInstance().addListener(mainController);

        SerialPortManager.getInstance().initialize();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(int i=1; i<=32; i++){
              //  basicSensor.setValue((basicSensor.getValue()+10.0));
                latitude.setValue((47.6597 + new Random().nextDouble()/100.0));
                longitude.setValue((-122.3357+new Random().nextDouble()/100.0));
//                gpsSensorv2.setPosition((47.6597 + new Random().nextDouble()/100.0),(-122.3357+new Random().nextDouble()/100.0));
//                gpsSensor.setPosition((47.6597 + new Random().nextDouble()/100.0),(-122.3357+new Random().nextDouble()/100.0));


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

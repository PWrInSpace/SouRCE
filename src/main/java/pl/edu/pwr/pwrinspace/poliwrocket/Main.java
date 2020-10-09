package pl.edu.pwr.pwrinspace.poliwrocket;

import com.sothawo.mapjfx.Projection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.*;

import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicButtonSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicSensorController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.*;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.ConfigurationSaveService;

import java.util.*;

public class Main extends Application {
    /**
     * Logger for the class
     */
    private static final Logger logger = LoggerFactory.getLogger(Main.class);


    @Override
    public void start(Stage primaryStage) throws Exception {

        try {
            ConfigurationSaveService configurationSaveService = new ConfigurationSaveService();
            configurationSaveService.saveToFile(ConfigurationSaveModel.defaultConfiguration());
            Configuration.getInstance().setupConfig(configurationSaveService.readFromFile());
            configurationSaveService.saveToFile(ConfigurationSaveModel.getConfigurationSaveModel(Configuration.getInstance()));

        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            logger.error(e.getCause().toString());
            logger.error(e.toString());
        }

        //FXMLLoader
        FXMLLoader loaderMain = new FXMLLoader(getClass().getClassLoader().getResource("MainView.fxml"));
        //FXMLLoader loaderMain = new FXMLLoader(getClass().getClassLoader().getResource("MainViewDAS.fxml"));

        FXMLLoader loaderData = new FXMLLoader(getClass().getClassLoader().getResource("DataView.fxml"));
        FXMLLoader loaderMap = new FXMLLoader(getClass().getClassLoader().getResource("MapViewNew.fxml"));
        FXMLLoader loaderPower = new FXMLLoader(getClass().getClassLoader().getResource("PowerView.fxml"));
        FXMLLoader loaderValves = new FXMLLoader(getClass().getClassLoader().getResource("ValvesView.fxml"));

        Scene scene = new Scene(loaderMain.load(), 1550, 750);

        MainController mainController = loaderMain.getController();
//        MainControllerDAS mainController = loaderMain.getController();
        mainController.initSubcenes(loaderData, loaderMap, loaderPower, loaderValves);

        DataController dataController = loaderData.getController();
        NewMapController mapController = loaderMap.getController();
        final Projection projection = getParameters().getUnnamed().contains("wgs84")
                ? Projection.WGS_84 : Projection.WEB_MERCATOR;
        mapController.initMapAndControls(projection);

        PowerController powerController = loaderPower.getController();
        ValvesController valvesController = loaderValves.getController();

        List<Triplet<BasicController, List<ISensorUI>,List<ICommand>>> controllersPairs = new LinkedList<>();
        controllersPairs.add(new Triplet<>(mainController, new LinkedList<>(), new LinkedList<>()));
        controllersPairs.add(new Triplet<>(dataController, new LinkedList<>(), new LinkedList<>()));
        controllersPairs.add(new Triplet<>(mapController, new LinkedList<>(), new LinkedList<>()));
        controllersPairs.add(new Triplet<>(powerController, new LinkedList<>(), new LinkedList<>()));
        controllersPairs.add(new Triplet<>(valvesController, new LinkedList<>(), new LinkedList<>()));

        for (int i = 0; i <controllersPairs.size() ; i++) {

            if (Configuration.getInstance().sensorRepository.getGpsSensor().getDestinationControllerNames().contains(controllersPairs.get(i).getValue0().getControllerNameEnum())) {
                Configuration.getInstance().sensorRepository.getGpsSensor().addListener(controllersPairs.get(i).getValue0());
            }
            if (Configuration.getInstance().sensorRepository.getGyroSensor().getDestinationControllerNames().contains(controllersPairs.get(i).getValue0().getControllerNameEnum())) {
                Configuration.getInstance().sensorRepository.getGyroSensor().addListener(controllersPairs.get(i).getValue0());
            }
            int finalI = i;
            Configuration.getInstance().sensorRepository.getAllBasicSensors().keySet().forEach(s -> {
                if (Configuration.getInstance().sensorRepository.getAllBasicSensors().get(s).getDestinationControllerNames().contains(controllersPairs.get(finalI).getValue0().getControllerNameEnum())) {
                    Configuration.getInstance().sensorRepository.getAllBasicSensors().get(s).addListener(controllersPairs.get(finalI).getValue0());
                    controllersPairs.get(finalI).getValue1().add(Configuration.getInstance().sensorRepository.getAllBasicSensors().get(s));
                }
            });

            for (int j = 0; j < Configuration.getInstance().commandsListValves.size(); j++) {
                if(Configuration.getInstance().commandsListValves.get(j).getDestinationControllerNames().contains(controllersPairs.get(i).getValue0().getControllerNameEnum())){
                    controllersPairs.get(i).getValue2().add(Configuration.getInstance().commandsListValves.get(j));
                }
            }
        }

        for (int i = 0; i <controllersPairs.size() ; i++) {
            if (controllersPairs.get(i).getValue0() instanceof BasicButtonSensorController) {
                ((BasicButtonSensorController) controllersPairs.get(i).getValue0()).injectSensorsModels(controllersPairs.get(i).getValue1());
                ((BasicButtonSensorController) controllersPairs.get(i).getValue0()).assignsCommands(controllersPairs.get(i).getValue2());
            } else if (controllersPairs.get(i).getValue0() instanceof BasicSensorController) {
                ((BasicSensorController) controllersPairs.get(i).getValue0()).injectSensorsModels(controllersPairs.get(i).getValue1());
            } else if (controllersPairs.get(i).getValue0() instanceof BasicButtonController) {
                ((BasicButtonController) controllersPairs.get(i).getValue0()).assignsCommands(controllersPairs.get(i).getValue2());
            }
        }

        primaryStage.setTitle("SouRCE");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        MessageParser.create(Configuration.getInstance().sensorRepository);
        MessageParser.getInstance().addListener(mainController);

        SerialPortManager.getInstance().initialize();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 1; i <= 32; i++) {
                Configuration.getInstance().sensorRepository.getSensorByName("lat").setValue((49.013517 + new Random().nextDouble() / 1000.0));
                Configuration.getInstance().sensorRepository.getSensorByName("long").setValue((8.404435 + new Random().nextDouble() / 1000.0));

                /*if(new Random().nextBoolean()){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }*/
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();//
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (int i = 1; i <= Integer.MAX_VALUE; i++) {
                Configuration.getInstance().sensorRepository.getSensorByName("Gyro X").setValue((new Random().nextDouble() * 100));
                Configuration.getInstance().sensorRepository.getSensorByName("Gyro Y").setValue((new Random().nextDouble() * 100));
                Configuration.getInstance().sensorRepository.getSensorByName("Gyro Z").setValue((new Random().nextDouble() * 100));

//                if(new Random().nextBoolean()){
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                try {
                    Thread.sleep(100);
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
    public void stop() {
        System.out.println("Stage is closing");
        SerialPortManager.getInstance().close();
        System.exit(-1);
    }
}

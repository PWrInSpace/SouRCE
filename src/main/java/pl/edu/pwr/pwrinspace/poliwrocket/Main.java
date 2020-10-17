package pl.edu.pwr.pwrinspace.poliwrocket;

import com.sothawo.mapjfx.Projection;
import gnu.io.NRSerialPort;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.javatuples.Triplet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.*;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.DiscordNotification;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.INotification;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.ISensor;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.ConfigurationSaveService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationFormatDiscordService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationInitService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationSendService;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.NotificationThread;

import java.util.*;

public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private ConfigurationSaveService configurationSaveService = new ConfigurationSaveService();
    private NotificationInitService notificationInitService;
    private NotificationSendService notificationSendService;
    private NotificationThread notificationThread;

    @Override
    public void start(Stage primaryStage) throws Exception {
        //now use only default config
        configurationSaveService.saveToFile(ConfigurationSaveModel.defaultConfiguration());
        String port = "";

        for(String s: NRSerialPort.getAvailableSerialPorts()){
            System.out.println("Availible port: "+s);
            port=s;
        }

        try {
            Configuration.getInstance().setupConfig(configurationSaveService.readFromFile());
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(Arrays.toString(e.getStackTrace()));
            logger.error(e.getCause().toString());
            logger.error(e.toString());

            configurationSaveService.saveToFile(ConfigurationSaveModel.defaultConfiguration());
            Configuration.getInstance().setupConfig(configurationSaveService.readFromFile());
        } finally {
            configurationSaveService.saveToFile(ConfigurationSaveModel.getConfigurationSaveModel(Configuration.getInstance()));
        }

        //FXMLLoader
        FXMLLoader loaderMain = new FXMLLoader(getClass().getClassLoader().getResource("MainView.fxml"));
        //FXMLLoader loaderMain = new FXMLLoader(getClass().getClassLoader().getResource("MainViewDAS.fxml"));

        FXMLLoader loaderData = new FXMLLoader(getClass().getClassLoader().getResource("DataView.fxml"));
        FXMLLoader loaderMap = new FXMLLoader(getClass().getClassLoader().getResource("MapViewNew.fxml"));
        FXMLLoader loaderPower = new FXMLLoader(getClass().getClassLoader().getResource("PowerView.fxml"));
        FXMLLoader loaderValves = new FXMLLoader(getClass().getClassLoader().getResource("ValvesView.fxml"));
        FXMLLoader loaderMoreData = new FXMLLoader(getClass().getClassLoader().getResource("MoreDataView.fxml"));
        FXMLLoader loaderAbort = new FXMLLoader(getClass().getClassLoader().getResource("AbortView.fxml"));
        FXMLLoader loaderStates = new FXMLLoader(getClass().getClassLoader().getResource("StatesView.fxml"));
        FXMLLoader loaderStart = new FXMLLoader(getClass().getClassLoader().getResource("StartControlView.fxml"));
        FXMLLoader loaderConnection = new FXMLLoader(getClass().getClassLoader().getResource("ConnectionView.fxml"));

        Scene scene = new Scene(loaderMain.load(), 1550, 750);

        MainController mainController = loaderMain.getController();
//        MainControllerDAS mainController = loaderMain.getController();
        mainController.initSubscenes(loaderData, loaderMap, loaderPower, loaderValves,loaderMoreData,
                loaderAbort,loaderStates,loaderStart,loaderConnection);

        DataController dataController = loaderData.getController();
        NewMapController mapController = loaderMap.getController();
        final Projection projection = getParameters().getUnnamed().contains("wgs84")
                ? Projection.WGS_84 : Projection.WEB_MERCATOR;
        mapController.initMapAndControls(projection);

        PowerController powerController = loaderPower.getController();
        ValvesController valvesController = loaderValves.getController();
        MoreDataController moreDataController = loaderMoreData.getController();
        AbortController abortController = loaderAbort.getController();
        StatesController statesController = loaderStates.getController();
        StartControlController startControlController = loaderStart.getController();
        ConnectionController connectionController = loaderConnection.getController();

        List<Triplet<BasicController, List<ISensor>, List<ICommand>>> controllersConfig = new LinkedList<>();
        controllersConfig.add(new Triplet<>(mainController, new LinkedList<>(), new LinkedList<>()));
        controllersConfig.add(new Triplet<>(dataController, new LinkedList<>(), new LinkedList<>()));
        controllersConfig.add(new Triplet<>(mapController, new LinkedList<>(), new LinkedList<>()));
        controllersConfig.add(new Triplet<>(powerController, new LinkedList<>(), new LinkedList<>()));
        controllersConfig.add(new Triplet<>(valvesController, new LinkedList<>(), new LinkedList<>()));
        controllersConfig.add(new Triplet<>(moreDataController, new LinkedList<>(), new LinkedList<>()));
        controllersConfig.add(new Triplet<>(abortController, new LinkedList<>(), new LinkedList<>()));
        controllersConfig.add(new Triplet<>(statesController, new LinkedList<>(), new LinkedList<>()));
        controllersConfig.add(new Triplet<>(startControlController, new LinkedList<>(), new LinkedList<>()));

        Configuration.setupApplicationConfig(controllersConfig);

        primaryStage.setTitle("SouRCE");
        primaryStage.setMaximized(true);
        primaryStage.setScene(scene);
        primaryStage.show();

        MessageParser.create(Configuration.getInstance().sensorRepository);
        MessageParser.getInstance().addListener(mainController);

//        SerialPortManager.getInstance().initialize();
        SerialPortManager.getInstance().addListener(connectionController);

        if(!Configuration.getInstance().DISCORD_TOKEN.equals("")){
            NotificationFormatDiscordService notificationFormatDiscordService = new NotificationFormatDiscordService(Configuration.getInstance().sensorRepository);
            INotification discord = new DiscordNotification(notificationFormatDiscordService);
            notificationInitService = new NotificationInitService(discord);
            notificationInitService.setup();
            notificationSendService = new NotificationSendService(discord);
            notificationThread = new NotificationThread(notificationSendService);
            HashMap<String, Integer> schedule = new HashMap<>();
            schedule.put("Data",10);
            schedule.put("Map",5);
//            notificationThread.setupSchedule(schedule);
        }


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
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notificationThread.run();
        }).start();
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            while (true) {
                Configuration.getInstance().sensorRepository.getSensorByName("Gyro X").setValue((new Random().nextDouble() * 121));
                Configuration.getInstance().sensorRepository.getSensorByName("Gyro Y").setValue((new Random().nextDouble() * 111));
                Configuration.getInstance().sensorRepository.getSensorByName("Gyro Z").setValue((new Random().nextDouble() * 107));
                Configuration.getInstance().sensorRepository.getSensorByName("Altitude").setValue((new Random().nextDouble() * 212+10));
                Configuration.getInstance().sensorRepository.getSensorByName("Velocity").setValue((new Random().nextDouble() * 100+200));
                Configuration.getInstance().sensorRepository.getSensorByName("Altitude2").setValue((new Random().nextDouble() * 10000 / 1.75));

                Configuration.getInstance().sensorRepository.getSensorByName("Ind 1").setValue((new Random().nextBoolean() ? 1.0 : 0.0));
                Configuration.getInstance().sensorRepository.getSensorByName("Ind 2").setValue((new Random().nextBoolean() ? 1.0 : 0.0));
                Configuration.getInstance().sensorRepository.getSensorByName("Ind 3").setValue((new Random().nextBoolean() ? 1.0 : 0.0));
                Configuration.getInstance().sensorRepository.getSensorByName("Ind 4").setValue((new Random().nextBoolean() ? 1.0 : 0.0));



//                if(new Random().nextBoolean()){
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                try {
                    Thread.sleep(1000);
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
        logger.info("Stage is closing");
        SerialPortManager.getInstance().close();
        System.exit(-1);
    }
}

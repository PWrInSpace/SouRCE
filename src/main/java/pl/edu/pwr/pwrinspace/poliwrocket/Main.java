package pl.edu.pwr.pwrinspace.poliwrocket;

import com.sothawo.mapjfx.Projection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.*;
import pl.edu.pwr.pwrinspace.poliwrocket.Controller.BasicController.BasicController;
import pl.edu.pwr.pwrinspace.poliwrocket.Event.Discord.NotificationDiscordEvent;
import pl.edu.pwr.pwrinspace.poliwrocket.Event.NotificationEvent;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.ConfigurationSaveModel;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.IMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.JsonMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.MessageParserEnum;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.StandardMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.DiscordNotification;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Notification.INotification;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort.SerialPortManager;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationFormatDiscordService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationFormatService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Notification.NotificationSendService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.ConfigurationSaveService;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.FrameSaveService;
import pl.edu.pwr.pwrinspace.poliwrocket.Thred.NotificationThread;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Main extends Application {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    private final ConfigurationSaveService configurationSaveService = new ConfigurationSaveService();
    private final FrameSaveService frameSaveService = new FrameSaveService();
    private NotificationSendService notificationSendService;
    private NotificationThread notificationThread;
    private NotificationFormatService notificationFormatService;
    private IMessageParser messageParser;
    private NotificationEvent notificationEvent;

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            //Now use only default config
            //configurationSaveService.saveToFile(ConfigurationSaveModel.defaultConfiguration());

            //Read config file
            try {
                Configuration.getInstance().setupConfigInstance(configurationSaveService.readFromFile());
            } catch (Exception e) {
                logger.error("Bad config file, overwritten by default and loaded");
                logger.error(e.getMessage());
                logger.error(Arrays.toString(e.getStackTrace()));
                logger.error(e.toString());
                configurationSaveService.persistOldConfig();
                configurationSaveService.saveToFile(ConfigurationSaveModel.defaultConfiguration());
                Configuration.getInstance().setupConfigInstance(configurationSaveService.readFromFile());
            }
//            finally {
//                configurationSaveService.saveToFile(ConfigurationSaveModel.getConfigurationSaveModel(Configuration.getInstance()));
//            }
            //--------------

            //FXMLLoader
            FXMLLoader loaderMain = new FXMLLoader(getClass().getClassLoader().getResource("MainView.fxml"));
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
            //--------------

            //Controllers
            MainController mainController = loaderMain.getController();
            mainController.initSubscenes(loaderData, loaderMap, loaderPower, loaderValves, loaderMoreData,
                    loaderAbort, loaderStates, loaderStart, loaderConnection);

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
            //--------------

            //Mapping sensors and commands to controllers
            List<BasicController> controllerList = new ArrayList<>();
            controllerList.add(mainController);
            controllerList.add(dataController);
            controllerList.add(mapController);
            controllerList.add(powerController);
            controllerList.add(valvesController);
            controllerList.add(moreDataController);
            controllerList.add(abortController);
            controllerList.add(statesController);
            controllerList.add(startControlController);
            controllerList.add(connectionController);
            Configuration.setupApplicationConfig(controllerList);
            //--------------

            //stage settings
            primaryStage.setTitle("SouRCE");
            primaryStage.setMaximized(true);
            primaryStage.setScene(scene);
            primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("Poliwrocket.png"))));
            primaryStage.show();
            //--------------

            //IMessageParser setup
            if(Configuration.getInstance().PARSER_TYPE == MessageParserEnum.JSON){
                messageParser = new JsonMessageParser(Configuration.getInstance().sensorRepository);
            } else if (Configuration.getInstance().PARSER_TYPE == MessageParserEnum.STANDARD) {
                messageParser = new StandardMessageParser(Configuration.getInstance().sensorRepository);
            } else {
                messageParser = new StandardMessageParser(Configuration.getInstance().sensorRepository);
            }
            messageParser.addListener(mainController);
            //--------------

            //FrameSaveService setup
            frameSaveService.writeFileHeader(Configuration.getInstance().FRAME_PATTERN);
            //--------------

            //SerialPortManager setup
            SerialPortManager.getInstance().setMessageParser(messageParser);
            SerialPortManager.getInstance().setFrameSaveService(frameSaveService);
            SerialPortManager.getInstance().addListener(connectionController);
            SerialPortManager.getInstance().addListener(mainController);
            //--------------

            //Notification setup
            if (!Configuration.getInstance().DISCORD_TOKEN.equals("")) {
                notificationFormatService = new NotificationFormatDiscordService(Configuration.getInstance().sensorRepository);
                notificationEvent = new NotificationDiscordEvent(notificationFormatService);
                INotification discord = new DiscordNotification(notificationEvent);
                discord.addListener(connectionController);
                notificationSendService = new NotificationSendService(discord, notificationFormatService);
                notificationThread = new NotificationThread(notificationSendService);
                notificationThread.setupSchedule(Configuration.getInstance().notificationSchedule);
                connectionController.injectNotification(notificationSendService, Configuration.getInstance().notificationMessageKeys, notificationThread);
                discord.setup();
            }
            //--------------


//            new Thread(() -> {
//                try {
//                    Thread.sleep(3000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                for (int i = 1; i <= 32; i++) {
//                    Configuration.getInstance().sensorRepository.getSensorByName("lat").setValue((49.013517 + (new Random().nextDouble() * 0.01)));
//                    Configuration.getInstance().sensorRepository.getSensorByName("long").setValue((8.404435 + (new Random().nextDouble() * 0.01)));
//
//                    try {
//                        Thread.sleep(1000);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();

//            new Thread(() -> {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                while (true) {
//                    Configuration.getInstance().sensorRepository.getSensorByName("Gyro X").setValue((new Random().nextDouble() * 121));
//                    Configuration.getInstance().sensorRepository.getSensorByName("Gyro Y").setValue((new Random().nextDouble() * 111));
//                    Configuration.getInstance().sensorRepository.getSensorByName("Gyro Z").setValue((new Random().nextDouble() * 107));
//                    Configuration.getInstance().sensorRepository.getSensorByName("Altitude").setValue((new Random().nextDouble() * 212 + 10));
//                    Configuration.getInstance().sensorRepository.getSensorByName("Velocity").setValue((new Random().nextDouble() * 100 + 200));
//                    Configuration.getInstance().sensorRepository.getSensorByName("Altitude2").setValue((new Random().nextDouble() * 10000 / 1.75));
//
//                    Configuration.getInstance().sensorRepository.getSensorByName("Ind 1").setValue((new Random().nextBoolean() ? 1.0 : 0.0));
//                    Configuration.getInstance().sensorRepository.getSensorByName("Ind 2").setValue((new Random().nextBoolean() ? 1.0 : 0.0));
//                    Configuration.getInstance().sensorRepository.getSensorByName("Ind 3").setValue((new Random().nextBoolean() ? 1.0 : 0.0));
//                    Configuration.getInstance().sensorRepository.getSensorByName("Ind 4").setValue((new Random().nextBoolean() ? 1.0 : 0.0));
//                    Configuration.getInstance().sensorRepository.getSensorByName("Main computer").setValue( 7.2 + (8.2 - 7.2) * new Random().nextDouble());
//                    Configuration.getInstance().sensorRepository.getSensorByName("Recovery 1").setValue( 7.2 + (8.2 - 7.2) * new Random().nextDouble());
//                    Configuration.getInstance().sensorRepository.getSensorByName("Recovery 2").setValue( 7.2 + (8.2 - 7.2) * new Random().nextDouble());
//
//
//                    try {
//                        Thread.sleep(500);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        logger.info("Stage is closing");
        if (SerialPortManager.getInstance().isPortOpen()) {
            SerialPortManager.getInstance().close();
        }
        System.exit(-1);
    }
}

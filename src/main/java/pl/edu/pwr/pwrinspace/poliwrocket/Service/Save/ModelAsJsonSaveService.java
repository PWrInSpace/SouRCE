//package pl.edu.pwr.pwrinspace.poliwrocket.Service.Save;
//
//import com.google.gson.Gson;
//import com.google.gson.stream.JsonReader;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import pl.edu.pwr.pwrinspace.poliwrocket.Model.BaseSaveModel;
//import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Command;
//import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ProtobufCommand;
//import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ProtobufSimpleCommand;
//import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.StandardCommand;
//import pl.edu.pwr.pwrinspace.poliwrocket.Model.Sensor.*;
//
//import java.io.*;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.nio.file.StandardCopyOption;
//import java.util.List;
//
//public class ModelAsJsonSaveService {
//
//    private static final Logger logger = LoggerFactory.getLogger(ModelAsJsonSaveService.class);
//    private final RuntimeTypeAdapterFactory<Sensor> sensorAdapterFactory = RuntimeTypeAdapterFactory.of(Sensor.class, "type")
//            .registerSubtype(Sensor.class, "Sensor") // done
//            .registerSubtype(FillingLevelSensor.class, "FillingLevelSensor") // done
//            .registerSubtype(TareSensor.class, "TareSensor") // done
//            .registerSubtype(AlertSensor.class, "AlertSensor") // done
//            .registerSubtype(TanwiarzSensor.class, "TanwiarzSensor") // done
//            .registerSubtype(PoteznyTanwiarzSensor.class, "PoteznyTanwiarzSensor") // done
//            .registerSubtype(SettingsSensor.class, "SettingsSensor") // done
//            .registerSubtype(CompositeBitSensor.class, "CompositeBitSensor") // done
//            .registerSubtype(ByteSensor.class, "ByteSensor") // done
//            .registerSubtype(TimerSensor.class, "TimerSensor"); // done
//
//    private final RuntimeTypeAdapterFactory<Command> commandAdapterFactory = RuntimeTypeAdapterFactory.of(Command.class, "type")
//            .registerSubtype(ProtobufCommand.class, "ProtobufCommand") // done
//            .registerSubtype(ProtobufSimpleCommand.class, "ProtobufSimpleCommand") // done
//            .registerSubtype(StandardCommand.class, "StandardCommand"); // done
//
//    // temp:
//    // true - zapisuje konfiguracje z do temp
//    // false - zapisuje konfiguracje do config
//    public void saveToFile(BaseSaveModel configuration, boolean temp) {
//        String configContent = new Gson().newBuilder()
//                .registerTypeAdapterFactory(sensorAdapterFactory)
//                .registerTypeAdapterFactory(commandAdapterFactory)
//                .excludeFieldsWithoutExposeAnnotation()
//                .setPrettyPrinting()
//                .disableHtmlEscaping()
//                .create()
//                .toJson(configuration);
//
//        Path configPath = Paths.get(configuration.getPath() + configuration.getFileName());
//        if (temp) configPath = Paths.get(configuration.getPath(), configuration.getTempFileName());
//
//        File configFile = new File(configPath.toString());
//
//        try (FileWriter configWriter = new FileWriter(configFile)) {
//            configWriter.write(configContent);
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//        }
//    }
//
//    public void persistOldFile(BaseSaveModel config) {
//        File dir = new File(config.getPath());
//        if(!dir.exists()) {
//            dir.mkdir();
//        }
//
//        File configFile = new File(config.getPath() + config.getFileName());
//        File copy = new File(config.getPath()  + config.getPersistPrefix() + config.getFileName());
//        try (FileInputStream fis = new FileInputStream(configFile);
//           FileOutputStream fos = new FileOutputStream(copy)) {
//            int len;
//            byte[] buffer = new byte[4096];
//            while ((len = fis.read(buffer)) > 0) {
//                fos.write(buffer, 0, len);
//            }
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//        }
//    }
//
//    // temp:
//    // true — czyta plik z temp
//    // false — ładuje plik config, a następnie nadpisuje nim temp i czyta plik temp
//    public <T extends BaseSaveModel> T readFromFile(T config, boolean temp) throws Exception {
//        if (!temp) {
//            Path configPath = Paths.get(config.getPath() + config.getFileName());
//            Path configTempPath = Paths.get(config.getPath() + config.getTempFileName());
//            Files.copy(configPath, configTempPath, StandardCopyOption.REPLACE_EXISTING);
//        }
//
//        File configFile = new File(config.getPath() + config.getTempFileName());
//
//        try (JsonReader reader = new JsonReader(new FileReader(configFile))) {
//            config = new Gson().newBuilder()
//                    .registerTypeAdapterFactory(sensorAdapterFactory)
//                    .registerTypeAdapterFactory(commandAdapterFactory)
//                    .excludeFieldsWithoutExposeAnnotation().create().fromJson(reader, config.getClass());
//        } catch (Exception e) {
//            logger.error(e.getMessage());
//            throw new Exception(e.getMessage());
//        }
//
//        return config;
//    }
//
//    public <T extends BaseSaveModel> void removeTempConfig(T config) {
//        File configTempFile = new File(config.getPath() + config.getTempFileName());
//        if (configTempFile.delete()) System.out.println("Removed temp file");
//        else System.out.println("Temp file not found");
//    }
//
//    public String commandToJson(Command command) {
//        return new Gson().newBuilder()
//                .registerTypeAdapterFactory(commandAdapterFactory)
//                .excludeFieldsWithoutExposeAnnotation()
//                .setPrettyPrinting()
//                .disableHtmlEscaping()
//                .create()
//                .toJson(command, Command.class);
//    }
//
//    public void addCommandToFile(BaseSaveModel config, Command command) {
//        String[] commandJson = commandToJson(command).split("\n");
//
//        Path configTempPath = Paths.get(config.getPath() + config.getTempFileName());
//
//        try {
//            List<String> configLines = Files.readAllLines(configTempPath);
//            int index = 0;
//            int squareBracketCounter = 1;
//            while(!configLines.get(index).contains("commandsList")) index++;
//            while(squareBracketCounter != 0) {
//                index++;
//                if(configLines.get(index).contains("[")) squareBracketCounter++;
//                if(configLines.get(index).contains("]")) squareBracketCounter--;
//            }
//            configLines.set(index-1, configLines.get(index-1) + ",");
//            for (String line : commandJson) {
//                configLines.add(index, "\t"+line);
//                index++;
//            }
//
//            Files.write(configTempPath, configLines);
//
//        } catch (FileNotFoundException e) {
//            logger.error("Config file not found");
//            logger.error(e.getMessage());
//        } catch (IOException e) {
//            logger.error("Error during modifying tempConfig");
//            logger.error(e.getMessage());
//        }
//    }
//
//    public void addDestinationToCommand(BaseSaveModel config, Command command, String controllerName) {
//    }
//
//    public void overrideConfig(BaseSaveModel config) {
//        Path configPath = Paths.get(config.getPath() + config.getFileName());
//        Path configTempPath = Paths.get(config.getPath() + config.getTempFileName());
//        try {
//            Files.copy(configTempPath, configPath, StandardCopyOption.REPLACE_EXISTING);
//        } catch (IOException e) {
//            logger.error(e.getMessage());
//            System.out.println(e.getMessage());
//        }
//    }
//}

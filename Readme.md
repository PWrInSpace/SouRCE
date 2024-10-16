# SoundingRocketControlEnvironment

SouRCE is the application for duplex communication with sounding rocket via serial port and external ground station.

## Installation
Requires Java 11, JavaFX 13, and Maven.

Download and install [Protobuf](https://grpc.io/docs/protoc-installation/).

Download [3dsModelImporterJFX](http://www.interactivemesh.org/models/jfx3dimporter.html) library for 3D model.

Add library to local mvn repository:
```bash
mvn install:install-file \
   -Dfile=$pathToFile \
   -DgroupId=com.interactivemesh.jfx \
   -DartifactId=importer \
   -Dversion=1.0 \
   -Dpackaging=jar \
   -DgeneratePom=true
```

## Usage

Adjust configuration in config.json file.

To regenerate classes for protobuf adjust `src\main\resources\lora.proto` file and run:
```bash
.\protoGenerate.sh
```
Add VM options:
```bash
--add-opens java.base/java.net=com.sothawo.mapjfx --add-opens java.base/java.lang.reflect=com.jfoenix --add-exports com.google.gson/com.google.gson.internal=pl.edu.pwr.pwrinspace.poliwrocket
```
To run the application on Linux download JavaFX and add modules:
```bash
--module-path $PATH_TO_JAVAFX_SDK/lib --add-modules=javafx.controls,javafx.fxml,javafx.base,javafx.graphics,javafx.web
```

To run the application with maps caching use VM options:
```bash
--add-opens java.base/java.net=com.sothawo.mapjfx
```

To use Discord bot specify discord token in config.json:
```bash
"DISCORD_TOKEN": "$YOUR_DISCORD_TOKEN",
"DISCORD_CHANNEL_NAME": "$YOUR_DISCORD_CHANNEL",
```

To use custom 3D model (```*.3DS```) name your file ```rocketModel.3DS``` and put in ```.\assets\rocketModel\```.  
It is important to put all model parts in a single group.


All flight data are store in ```.\flightData```.
## Deploy

To deploy the application use:
```bash
mvn package

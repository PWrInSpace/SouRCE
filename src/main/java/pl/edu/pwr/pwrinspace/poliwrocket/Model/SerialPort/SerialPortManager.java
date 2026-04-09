package pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort;

import com.google.common.primitives.Bytes;
import gnu.io.NRSerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import javafx.beans.InvalidationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.Frame;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.IMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.FrameSaveService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class SerialPortManager implements SerialPortEventListener, ISerialPortManager {

    private final List<InvalidationListener> observers = new ArrayList<>();
    private final List<InvalidationListener> portStatusObservers = new ArrayList<>();

    private NRSerialPort serialPort;
    private String PORT_NAME = "COM3";
    private int DATA_RATE = 115200;
    private final Logger log = LoggerFactory.getLogger(getClass().getName());
    private OutputStream outputStream;
    private InputStream inputStream;
    private SerialWriter serialWriter;
    private boolean isPortOpen = false;
    private FrameSaveService frameSaveService;
    private IMessageParser messageParser;
    private String lastMessage = "";
    protected static String msgPrefix = Configuration.getInstance().MSG_PREFIX;

    private static boolean isInitialized = false;

    private static class Holder {
        private static final SerialPortManager INSTANCE = new SerialPortManager();
    }

    private SerialPortManager() {
        if (isInitialized) {
            throw new IllegalStateException("Singleton already constructed");
        }
        isInitialized = true;
    }

    public static SerialPortManager getInstance() {
        return Holder.INSTANCE;
    }

    @Override
    public void addListener(InvalidationListener invalidationListener) {
        observers.add(invalidationListener);
    }

    @Override
    public void removeListener(InvalidationListener invalidationListener) {
        observers.remove(invalidationListener);
    }

    @Override
    public void setFrameSaveService(FrameSaveService frameSaveService) {
        this.frameSaveService = frameSaveService;
    }

    @Override
    public void addPortStatusListener(InvalidationListener invalidationListener) {
        this.portStatusObservers.add(invalidationListener);
    }

    private void notifyObserver() {
        for (InvalidationListener obs : observers) {
            obs.invalidated(this);
        }
    }

    private void notifyPortStatusObserver() {
        for (InvalidationListener obs : portStatusObservers) {
            obs.invalidated(this);
        }
    }

    @Override
    public void setMessageParser(IMessageParser messageParser) {
        this.messageParser = messageParser;
    }

    @Override
    public void initialize(String portName, int dataRate) {
        this.PORT_NAME = portName;
        this.DATA_RATE = dataRate;
        this.initialize();
    }

    @Override
    public boolean isPortOpen() {
        return this.isPortOpen;
    }

    @Override
    public void initialize() {
        if(messageParser != null) {
            try {
                // otwieramy i konfigurujemy port
                serialPort = new NRSerialPort(PORT_NAME, DATA_RATE);
                serialPort.connect();
                if(serialPort.isConnected()) {
                    // strumień wejścia
                    inputStream = serialPort.getInputStream();

                    //strumień wyjścia
                    outputStream = serialPort.getOutputStream();
                    serialWriter = new SerialWriter(outputStream);
                    Thread writerThread = new Thread(serialWriter);
                    writerThread.setDaemon(true);
                    writerThread.start();

                    // dodajemy słuchaczy zdarzeń
                    serialPort.addEventListener(this);
                    serialPort.notifyOnDataAvailable(true);
                } else {
                    try {
                        serialPort.disconnect();
                    } catch (NullPointerException e) {
                        serialPort = new NRSerialPort(PORT_NAME, DATA_RATE);
                    }
                }
                isPortOpen = serialPort.isConnected();
            } catch (Exception e) {
                isPortOpen = serialPort.isConnected();
                log.warn(e.toString());
            } finally {
                lastMessage = "";
                notifyObserver();
                notifyPortStatusObserver();
                log.info("Serial port status: {}", isPortOpen);
            }
        } else {
            isPortOpen = serialPort.isConnected();
            notifyObserver();
            notifyPortStatusObserver();
            log.warn("IMessageParser not set");
            log.info("Serial port status: {}", isPortOpen);
        }
        if(frameSaveService == null) {
            log.warn("FrameSaveService not set");
        }
    }

    @Override
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.disconnect();
            log.info("Serial port closed.");
            isPortOpen = serialPort.isConnected();
            notifyObserver();
            notifyPortStatusObserver();
        }
    }

    @Override
    public void serialEvent(SerialPortEvent oEvent) {
        synchronized (messageParser) {
            if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
                log.info("DATA RECEIVED");
                try {
                    log.info("Available bytes: {}", this.inputStream.available());
                    Frame frame;
                    byte[] buffer;

                    if (Configuration.getInstance().BUFFER_SIZE != 0) {
                        log.info("Reading with buffer size: {}", Configuration.getInstance().BUFFER_SIZE);
                        buffer = this.inputStream.readNBytes(Configuration.getInstance().BUFFER_SIZE);
                    } else {
                        if (this.inputStream.available() >= 5) {
                        byte header = (byte) this.inputStream.read();
                        if (header == 0x32) {
                            byte cmd = (byte) this.inputStream.read();
                            int dataLen = this.inputStream.read();
                            log.info("Header: 0x32, cmd: {}, dataLen: {}", String.format("0x%02X", cmd), dataLen);

                            buffer = this.inputStream.readNBytes(dataLen);
                            // todo dodać crc
                        } else {
                            log.error("Wrong header, is {}, should be 0x32", header);
                            return;
                        }
//                        buffer = new byte[512];
//                        int length = 0;
//
//                        while(this.inputStream.available() > 0) {
//                            buffer[length] = (byte)this.inputStream.read();
//                            length++;
//
//                            if(length == 512) {
//                                log.log(Level.INFO, "LENGTH IS 512");
//                                return;
//                            }
//
//                            if(this.inputStream.available() == 0) {
//                                Thread.sleep(1);
//                            }
//                        }
//                        buffer = Arrays.copyOfRange(buffer, msgPrefix.length(), length);
                        } else {
                            log.error("Received data is to small, is {}, should be 5", this.inputStream.available());
                            return;
                        }
                    }
                    log.info("RECEIVED DATA: {}", new String(buffer));
                    log.info("DATA LENGTH: {}", buffer.length);

                    frame = new Frame(buffer, Instant.now());

                    messageParser.parseMessage(frame);
                    if(frameSaveService != null) {
                        if(frame.getFormattedContent() == null) {
                            frame.setFormattedContent(frame.getStringContent());
                        }
                        frameSaveService.saveFrameToFile(frame);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }

            }
        }
    }

    public void write(String message) {
        System.out.println(message);
        if(serialWriter == null) {
            log.warn("Not connected");
            return;
        }
        log.info("Written: {}", message);
        serialWriter.send(message);
        this.lastMessage = message;
        notifyObserver();
    }

    public void write(ICommand command) {
        System.out.println(command.getCommandValueAsString());
        if(serialWriter == null) {
            log.info("Serial port would send this data");
            var bytes = command.getCommandValueAsBytes(Configuration.getInstance().isForceCommandsActive());
            var bytesCRC = SerialWriter.addHeaderCmdLengthCRC(bytes);
            for (byte b : bytes) {
                System.out.printf("0x%02X ", b);
            }
//            System.out.printf("0x%02X", (byte) '\n');
            System.out.println();
            for (byte b : bytesCRC) {
                System.out.printf("0x%02X ", b);
            }
//            System.out.printf("0x%02X", (byte) '\n');
            System.out.println();
            log.warn("Not connected");
            return;
        }
        var msg = command.getCommandValueAsString() + '\n';
        log.info("Written: {}", msg);
        serialWriter.send(command.getCommandValueAsBytes(Configuration.getInstance().isForceCommandsActive()));
//        serialWriter.sendSeparator();
        this.lastMessage = msg;
        notifyObserver();
    }

    public void writeWithoutCRC(String message) {
        if(serialWriter == null) {
            log.warn("Not connected");
            return;
        }
        log.info("Written: {}", message);
        serialWriter.sendWithoutCRC(message);
        this.lastMessage = message;
        notifyObserver();
    }

    @Override
    public String getLastSend() {
        return this.lastMessage;
    }

    public SerialWriter getSerialWriter() {
        return serialWriter;
    }

    public static class SerialWriter implements Runnable {
        OutputStream out;

        private static final Logger logger = LoggerFactory.getLogger(SerialWriter.class);

        public SerialWriter (OutputStream out) {
            this.out = out;
        }

        @Override
        public void run() {
            try {
                sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }
        }

        public void send(String msg) {
            send(msg.getBytes());
        }

        public void send(byte[] msg) {
            try {
                //out.write(msg);
                var finalMsg = addHeaderCmdLengthCRC(msg);
                out.write(finalMsg);
//                logger.info("Written msg: {}",msg);
//                logger.info("Written with prefix and crc: {}",finalMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendSeparator() {
            try {
                out.write('\n');
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendWithoutCRC(String msg) {
            sendWithoutCRC(msg.getBytes());
        }

        public void sendWithoutCRC(byte[] msg) {
            try {
                out.write(msg);
                logger.info("Written msg: {}", msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public static byte[] getMessageCRC(byte[] msg) {
            int messageCounter = 0;
            for (byte msgByte : msg) {
                messageCounter += msgByte;
            }

            return new byte[]{ (byte)(messageCounter % 512) };
        }

        public static byte[] addHeaderCmdLengthCRC(byte[] msg) {
            int dataLen = msg.length;
            byte[] cmd = {SerialPortCommand.CMD_LORA_TX.getCode()};
            byte[] dataLenByte = {(byte) dataLen};
            //todo dodać sprawdzanie czy długość się nie zgettciła
            return Bytes.concat(new byte[]{0x32}, cmd, dataLenByte, msg, getMessageCRC(msg));
        }
    }
}

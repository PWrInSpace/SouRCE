package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import gnu.io.NRSerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import javafx.beans.InvalidationListener;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.FrameSaveService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Thread.sleep;

public class SerialPortManager implements SerialPortEventListener, ISerialPortManager {

    public List<InvalidationListener> observers = new ArrayList<>();

    private NRSerialPort serialPort;
    // Na windowsie domyślnie posługujemy się portem COM3
    protected String PORT_NAME = "COM3";
    private final Logger log = Logger.getLogger(getClass().getName()); //java.util.logging.Logger
    private OutputStream outputStream;
    private InputStream inputStream;
    protected SerialWriter serialWriter;
    private boolean isPortOpen = false;
    private FrameSaveService frameSaveService = new FrameSaveService();

    private SerialPortManager() {
        if (Holder.INSTANCE != null) {
            throw new IllegalStateException("Singleton already constructed");
        }
    }

    public static ISerialPortManager getInstance() {
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

    private void notifyObserver() {
        for (InvalidationListener obs : observers) {
            obs.invalidated(this);
        }
    }

    private static class Holder {
        private static final SerialPortManager INSTANCE = new SerialPortManager();
    }

    //    public void speak(){
//        Thread speaker = new Thread(){
//            @Override
//            public void run() {
//                try
//                {
//                    // set property as Kevin Dictionary
//                    System.setProperty("freetts.voices",
//                            "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
//
//                    // Register Engine
//                    Central.registerEngineCentral
//                            ("com.sun.speech.freetts.jsapi.FreeTTSEngineCentral");
//
//                    // Create a Synthesizer
//                    Synthesizer synthesizer =
//                            Central.createSynthesizer(new SynthesizerModeDesc(Locale.US));
//
//                    // Allocate synthesizer
//
//                    synthesizer.allocate();
//
//                    // Resume Synthesizer
//                    synthesizer.resume();
//                    synthesizer.speakPlainText(msgToSay, null);
//                    synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
//                    /*
//                    // speaks the given text until queue is empty.
//                    synthesizer.speakPlainText("PoliWRocket Mission Control System Started", null);
//                    synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
//                    synthesizer.speakPlainText("Altitiude 700 meters.", null);
//                    synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
//                    synthesizer.speakPlainText("Apogee detected.", null);
//                    synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);
//                    synthesizer.speakPlainText("Main parachute deployed.", null);
//                    synthesizer.waitEngineState(Synthesizer.QUEUE_EMPTY);*/
//                    // Deallocate the Synthesizer.
//                    synthesizer.deallocate();
//                }
//
//                catch (Exception e)
//                {
//                    e.printStackTrace();
//                }
//            }
//        };
//        speaker.start();
//    }


    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 4000;
    /**
     * Default bits per second for COM port.
     */
    private int DATA_RATE = 115200;
//    private static final int DATA_RATE = 9600; ->poprzednia wersja
    //Singleton pattern

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
                (new Thread(serialWriter)).start();

                // dodajemy słuchaczy zdarzeń
                serialPort.addEventListener(this);
                serialPort.notifyOnDataAvailable(true);
            } else {
                serialPort.disconnect();
            }
            isPortOpen = serialPort.isConnected();
        } catch (Exception e) {
            isPortOpen = serialPort.isConnected();
            log.log(Level.WARNING,e.toString());
        } finally {
            notifyObserver();
            log.log(Level.INFO, "Serialport status open: {}", isPortOpen);
        }
    }

    @Override
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.disconnect();
            log.log(Level.INFO, "Serialport closed.");
            isPortOpen = serialPort.isConnected();
            notifyObserver();
        }
    }

    /**
     * Metoda nasłuchuje na dane na wskazanym porcie i wyświetla je w konsoli
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                byte[] buffer = new byte[1024];
                int length = this.inputStream.read(buffer);
                Frame frame = new Frame(new String(buffer, 0, length), Instant.now());
                MessageParser.getInstance().parseMessage(frame);
                frameSaveService.saveFrameToFile(frame);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public synchronized void write(String message) {
        log.log(Level.INFO, "Written: {0}", message);
        serialWriter.send(message);
    }

    /**
     *
     */
    public static class SerialWriter implements Runnable {
        OutputStream out;

        private static final org.slf4j.Logger logger = LoggerFactory.getLogger(SerialWriter.class);

        public SerialWriter(OutputStream out) {
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

        public synchronized void send(String msg) {
            try {
                out.write(msg.getBytes());
                logger.info("Written: {}",msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public synchronized void sendInt(int msg) {
            try {
                out.write(msg);
                logger.info("Written: {}",msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

package pl.edu.pwr.pwrinspace.poliwrocket.Model;

import gnu.io.*;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.lang.Double.parseDouble;
import static java.lang.Thread.sleep;

public class SerialPortManager implements SerialPortEventListener {

    private NRSerialPort serialPort;
    // Na windowsie domyślnie posługujemy się portem COM3
    protected String PORT_NAME = "COM5";
    private final Logger log = Logger.getLogger(getClass().getName()); //java.util.logging.Logger
    private BufferedReader input;
    private OutputStream outputStream;
    private InputStream inputStream;
    protected SerialWriter serialWriter;
    private StringBuilder msgAll = new StringBuilder();
    private String pattern = "\"yyyy-MM-dd HH:mm:ss.S\"";
    private DateFormat dateFormat = new SimpleDateFormat(pattern);
    private String patternFile = "yyyy-MM-dd HH-mm-ss";
    private DateFormat dateFormatFile = new SimpleDateFormat(patternFile);
    FileWriter flightData;
    private String delims = "[&]+";
    private String delims2 = ";";
    public boolean isPortOpen = false;;
    public GyroSensor gyro = new GyroSensor();

    private SerialPortManager() {
        if (Holder.INSTANCE != null) {
            throw new IllegalStateException("Singleton already constructed");
        }
    }

    public static SerialPortManager getInstance() {
        return Holder.INSTANCE;
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


    /** Milliseconds to block while waiting for port open */
    private static final int TIME_OUT = 4000;
    /** Default bits per second for COM port. */
    private static final int DATA_RATE = 9600;
    //Singleton pattern



    public void initialize() {
        try {
            // otwieramy i konfigurujemy port
            serialPort = new NRSerialPort(PORT_NAME, DATA_RATE);
            serialPort.connect();

            // strumień wejścia
            inputStream = serialPort.getInputStream();

            //strumień wyjścia
            outputStream = serialPort.getOutputStream();
            serialWriter =  new SerialWriter(outputStream);
            (new Thread(serialWriter)).start();

            // dodajemy słuchaczy zdarzeń
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);
        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.disconnect();
            log.log(Level.INFO,"Serialport closed.");
            isPortOpen=false;
        }
    }

    /**
     * Metoda nasłuchuje na dane na wskazanym porcie i wyświetla je w konsoli
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            byte[] buffer = new byte[2048];
            int len = -1;
            msgAll.delete(0,msgAll.length());
            try
            {
                while ( ( len = this.inputStream.read(buffer)) > 0 )
                {
                    String msg = new String(buffer, 0, len);
                    System.out.print(msg);
                    msgAll.append(msg);
                    String[] splited = msg.split(",");
                    gyro.update(parseDouble(splited[0]), parseDouble(splited[1]), parseDouble(splited[2]));

                    MessageParser.getInstance().parseMessage(buffer,len);
                }
                System.out.println("done");
            }
            catch ( IOException e )
            {
                e.printStackTrace();
            }

        }
    }

    /** */
    public static class SerialWriter implements Runnable
    {
        OutputStream out;

        public SerialWriter ( OutputStream out )
        {
            this.out = out;
        }

        @Override
        public void run () {

            try {

                sleep(100);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public synchronized void send(String msg) {
            try {
                out.write(msg.getBytes());
                System.out.println("writed: " + msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public synchronized void sendInt(int msg) {
            try {
                out.write(msg);
                System.out.println("writed: " + msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


  /*      public synchronized  void writeData() {
            try
            {
                int c = 0;
                while ( ( c = System.in.read()) > -1 )
                {
                    this.out.write(c);
                }
            }
            catch ( IOException e )
            {
                e.printStackTrace();
                System.exit(-1);
            }
        }*/
    }
}

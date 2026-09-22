package pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort;

import gnu.io.NRSerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import javafx.beans.InvalidationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.ICommand;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Csp.CspNode;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Csp.CspPacket;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.Frame;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.MessageParser.IMessageParser;
import pl.edu.pwr.pwrinspace.poliwrocket.Service.Save.FrameSaveService;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Serial link manager, now backed by CSP over KISS instead of the old
 * msgPrefix + checksum framing. Public API is unchanged on purpose so the
 * rest of the app (anything calling SerialPortManager.getInstance()) does
 * not need to be touched.
 */
public class SerialPortManager implements SerialPortEventListener, ISerialPortManager {

    /* ---------------------------------------------------------------------
     * CSP addressing - MUST match the firmware in csp_task.c (or whatever
     * the real flight node's csp_task.c-equivalent config is). Getting any
     * of these wrong means packets are silently dropped or routed nowhere -
     * there's no error on the wire, CSP just doesn't deliver.
     * --------------------------------------------------------------------- */

    /** CSP address of the board/rocket this ground station talks to. */
    public static final int CSP_TARGET_ADDRESS = 0; // TODO: confirm against firmware's csp_set_address / CSP_DEMO_ADDRESS
    /** CSP address of THIS ground station node - must be unique on the link. */
    public static final int CSP_MY_ADDRESS = 8; // TODO: pick something that doesn't collide with any board
    /** Destination port telemetry/log frames arrive FROM the rocket on. */
    public static final int CSP_TELEMETRY_PORT = 10; // TODO: was CSP_DEMO_PORT in the demo - confirm real port
    /** Destination port outgoing commands are addressed TO on the rocket. */
    public static final int CSP_COMMAND_PORT = 10; // TODO: may be a different port than telemetry
    /** Our own ephemeral source port for outgoing traffic (CSP convention: 48-63). */
    public static final int CSP_SOURCE_PORT = 63;
    /** Priority used for outgoing command packets (CSP: 0=highest .. 3=lowest, CSP_PRIO_NORM=2 in libcsp). */
    public static final int CSP_PRIORITY = 2;
    /** Whether the firmware's KISS interface appends a CRC32 trailer (CSP_ENABLE_CRC32 build option). */
    public static final boolean CSP_USE_CRC32 = true; // TODO: verify against firmware build config

    private final List<InvalidationListener> observers = new ArrayList<>();
    private final List<InvalidationListener> portStatusObservers = new ArrayList<>();

    private NRSerialPort serialPort;
    private String PORT_NAME = "COM3";
    private int DATA_RATE = 115200;
    private final Logger log = LoggerFactory.getLogger(SerialPortManager.class);
    private CspNode cspNode;
    private CspWriter cspWriter;
    private boolean isPortOpen = false;
    private FrameSaveService frameSaveService;
    private IMessageParser messageParser;
    private final Object LOCK = new Object();
    private String lastMessage = "";

    private SerialPortManager() {
        if (getInstance() != null) {
            throw new IllegalStateException("Singleton already constructed");
        }
    }

    public static SerialPortManager getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final SerialPortManager INSTANCE = new SerialPortManager();
    }

    public void addListener(InvalidationListener invalidationListener) {
        observers.add(invalidationListener);
    }

    public void removeListener(InvalidationListener invalidationListener) {
        observers.remove(invalidationListener);
    }

    public void setFrameSaveService(FrameSaveService frameSaveService) {
        this.frameSaveService = frameSaveService;
    }

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
        synchronized (LOCK) {
            this.messageParser = messageParser;
        }
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
        if (messageParser == null) {
            log.warn("IMessageParser not set");
        }
        try {
            serialPort = new NRSerialPort(PORT_NAME, DATA_RATE);
            serialPort.connect();
            if (serialPort.isConnected()) {
                cspNode = new CspNode(serialPort.getOutputStream(), CSP_MY_ADDRESS, CSP_USE_CRC32);
                cspNode.registerPortHandler(CSP_TELEMETRY_PORT, this::onCspPacket);

                cspWriter = new CspWriter(cspNode);
                Thread writerThread = new Thread(cspWriter, "csp-writer");
                writerThread.setDaemon(true);
                writerThread.start();

                // NRSerialPort still delivers bytes via the classic event listener -
                // we just feed them into the KISS decoder now instead of parsing
                // msgPrefix/CRC ourselves.
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
            isPortOpen = serialPort != null && serialPort.isConnected();
            log.warn(e.toString());
        } finally {
            lastMessage = "";
            notifyObserver();
            notifyPortStatusObserver();
        }
        if (frameSaveService == null) {
            log.warn("FrameSaveService not set");
        }
    }

    @Override
    public synchronized void close() {
        if (cspWriter != null) {
            cspWriter.stop();
            cspWriter = null;
        }
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.disconnect();
            isPortOpen = serialPort.isConnected();
            notifyObserver();
            notifyPortStatusObserver();
        }
    }

    @Override
    public void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() != SerialPortEvent.DATA_AVAILABLE || cspNode == null) {
            return;
        }
        try {
            byte[] buf = new byte[512];
            int available;
            while ((available = serialPort.getInputStream().available()) > 0) {
                int n = serialPort.getInputStream().read(buf, 0, Math.min(available, buf.length));
                if (n > 0) {
                    cspNode.feed(buf, 0, n);
                }
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Called by CspNode whenever a packet addressed to CSP_TELEMETRY_PORT arrives.
     * Runs on the serial event thread, same as the old serialEvent() body did -
     * kept under LOCK for the same reason the original code used it (messageParser
     * and frameSaveService are not necessarily thread-safe against setMessageParser()).
     */
    private void onCspPacket(CspPacket packet) {
        synchronized (LOCK) {
            if (messageParser == null) {
                log.warn("Dropping CSP packet from addr {} - no IMessageParser set", packet.header.source);
                return;
            }
            log.info("CSP RX from addr {} dport {}: {} bytes",
                    packet.header.source, packet.header.destinationPort, packet.payload.length);

            Frame frame = new Frame(packet.payload, Instant.now());
            messageParser.parseMessage(frame);

            if (frameSaveService != null) {
                if (frame.getFormattedContent() == null) {
                    frame.setFormattedContent(frame.getStringContent());
                }
                frameSaveService.saveFrameToFile(frame);
            }
        }
    }

    public void write(String message) {
        write(message.getBytes());
    }

    /**
     * Kept for source compatibility with callers from the old prefix+checksum
     * protocol, where "without CRC" meant skipping the manual checksum byte.
     * Under CSP there's no such distinction anymore - framing/CRC (if enabled)
     * is handled uniformly by CspNode/Kiss for every outgoing packet - so this
     * is just an alias for write(). Safe to keep calling it from existing code;
     * feel free to grep for callers and switch them to write() when convenient,
     * there's no rush.
     */
    public void writeWithoutCRC(String message) {
        write(message.getBytes());
    }

    public void write(ICommand command) {
        log.info("Written command: {}", command.getCommandValueAsString());
        write(command.getCommandValueAsBytes(Configuration.getInstance().isForceCommandsActive()));
    }

    private void write(byte[] payload) {
        if (cspWriter == null) {
            log.warn("Not connected");
            return;
        }
        cspWriter.send(CSP_TARGET_ADDRESS, CSP_COMMAND_PORT, CSP_SOURCE_PORT, CSP_PRIORITY, payload);
        this.lastMessage = new String(payload);
        notifyObserver();
    }

    @Override
    public String getLastSend() {
        return this.lastMessage;
    }

    /**
     * Queued async writer, playing the same role the old SerialWriter did:
     * keeps outgoing traffic off the calling (often JavaFX) thread and
     * serializes writes. Unlike the old one it no longer needs to build a
     * checksum/prefix itself - CspNode.send() already handles CSP header +
     * KISS framing (+ CRC32 if enabled) for every message uniformly, so the
     * old StandardCommand-vs-other CRC branching is gone.
     */
    private static class CspWriter implements Runnable {

        private static final class OutgoingMessage {
            final int destination;
            final int destinationPort;
            final int sourcePort;
            final int priority;
            final byte[] payload;

            OutgoingMessage(int destination, int destinationPort, int sourcePort, int priority, byte[] payload) {
                this.destination = destination;
                this.destinationPort = destinationPort;
                this.sourcePort = sourcePort;
                this.priority = priority;
                this.payload = payload;
            }
        }

        private static final Logger logger = LoggerFactory.getLogger(CspWriter.class);

        private final CspNode node;
        private final BlockingQueue<OutgoingMessage> queue = new LinkedBlockingQueue<>();
        private volatile Thread runningThread;

        CspWriter(CspNode node) {
            this.node = node;
        }

        void send(int destination, int destinationPort, int sourcePort, int priority, byte[] payload) {
            queue.add(new OutgoingMessage(destination, destinationPort, sourcePort, priority, payload));
        }

        void stop() {
            if (runningThread != null) {
                runningThread.interrupt();
            }
        }

        @Override
        public void run() {
            runningThread = Thread.currentThread();
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    OutgoingMessage m = queue.take();
                    node.send(m.destination, m.destinationPort, m.sourcePort, m.priority, m.payload);
                    logger.info("Sent CSP packet: dst={} dport={} {} bytes", m.destination, m.destinationPort, m.payload.length);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (IOException e) {
                    logger.error("Error writing CSP packet: {}", e.getMessage());
                }
            }
        }
    }
}
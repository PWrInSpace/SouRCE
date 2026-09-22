package pl.edu.pwr.pwrinspace.poliwrocket.Model.Csp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Minimal Java-side CSP node: KISS framing over a serial link, header pack/unpack,
 * and per-port callback dispatch. Deliberately does NOT implement CSP's RDP
 * (reliable, connection-oriented transport) - libcsp connections such as the
 * one csp_server_task() accepts via csp_accept()/csp_listen() do not require a
 * handshake unless RDP is explicitly requested (CSP_O_RDP), so a well-formed
 * connectionless packet addressed to a bound+listening port is enough to be
 * accepted on the other end. This matches how csp_task.c's demo server works.
 *
 * Usage:
 *   CspNode node = new CspNode(outputStream, /* myAddress * / 1, /* useCrc32 * / true);
 *   node.registerPortHandler(CspNode.CSP_DEMO_PORT, packet -> ...);
 *   node.attachReader(inputStream); // or call node.feed(...) yourself
 *   node.send(0, CspNode.CSP_DEMO_PORT, 63, 1, "Hello world A\0".getBytes());
 */
public class CspNode {

    private static final Logger log = LoggerFactory.getLogger(CspNode.class);

    /** Must match CSP_DEMO_PORT in csp_task.c */
    public static final int CSP_DEMO_PORT = 10;
    /** Must match CSP_LORA_FWD_PORT in csp_task.c */
    public static final int CSP_LORA_FWD_PORT = 11;
    /** Standard libcsp service-handler ping port. */
    public static final int CSP_PORT_PING = 1;

    private final OutputStream out;
    private final int myAddress;
    private final boolean useCrc32;
    private final Kiss.Decoder decoder;
    private final Object writeLock = new Object();

    private final Map<Integer, CspPortHandler> handlers = new ConcurrentHashMap<>();
    // correlates outstanding request/reply pairs, keyed by our own source port
    private final Map<Integer, LinkedBlockingQueue<CspPacket>> pending = new ConcurrentHashMap<>();

    private volatile Thread readerThread;

    public CspNode(OutputStream out, int myAddress, boolean useCrc32) {
        this.out = out;
        this.myAddress = myAddress;
        this.useCrc32 = useCrc32;
        this.decoder = new Kiss.Decoder(this::onRawFrame);
    }

    /** Register a callback for packets addressed to the given local destination port. */
    public void registerPortHandler(int destinationPort, CspPortHandler handler) {
        handlers.put(destinationPort, handler);
    }

    /**
     * Feed raw bytes as read from the serial InputStream. Call this from
     * wherever you already pull bytes off the wire (e.g. inside a
     * SerialPortEventListener, analogous to serialEvent() in SerialPortManager).
     */
    public void feed(byte[] data, int offset, int len) {
        decoder.feed(data, offset, len);
    }

    public void feed(byte[] data) {
        feed(data, 0, data.length);
    }

    /**
     * Convenience: spins a daemon thread that blockingly reads from the given
     * InputStream and feeds the decoder. Use this OR feed() manually, not both,
     * for a given stream.
     */
    public void attachReader(InputStream in) {
        readerThread = new Thread(() -> {
            byte[] buf = new byte[512];
            try {
                int n;
                while (!Thread.currentThread().isInterrupted() && (n = in.read(buf)) >= 0) {
                    if (n > 0) {
                        feed(buf, 0, n);
                    }
                }
            } catch (IOException e) {
                log.warn("CSP reader thread stopped: {}", e.getMessage());
            }
        }, "csp-kiss-reader");
        readerThread.setDaemon(true);
        readerThread.start();
    }

    public void stopReader() {
        if (readerThread != null) {
            readerThread.interrupt();
        }
    }

    private void onRawFrame(byte[] raw) {
        CspPacket packet;
        try {
            packet = CspPacket.fromWireBytes(raw, useCrc32);
        } catch (IllegalArgumentException e) {
            log.warn("Dropping malformed CSP frame: {}", e.getMessage());
            return;
        }

        log.debug("RX {}", packet);

        // Route replies to anyone blocked in requestReply() first...
        LinkedBlockingQueue<CspPacket> q = pending.get(packet.header.destinationPort);
        if (q != null) {
            q.offer(packet);
        }

        // ...then always dispatch to a registered application handler, if any.
        CspPortHandler handler = handlers.get(packet.header.destinationPort);
        if (handler != null) {
            handler.onPacket(packet);
        } else if (q == null) {
            log.debug("No handler for CSP dport {}", packet.header.destinationPort);
        }
    }

    /** Sends a packet. Thread-safe (serializes writes the same way csp_usart_lock does on the ESP32 side). */
    public void send(int destination, int destinationPort, int sourcePort, int priority, byte[] payload)
            throws IOException {
        CspHeader header = CspHeader.of(priority, myAddress, destination, destinationPort, sourcePort);
        CspPacket packet = new CspPacket(header, payload);
        byte[] framed = Kiss.encode(packet.toWireBytes(useCrc32));

        log.debug("TX {}", packet);
        synchronized (writeLock) {
            out.write(framed);
            out.flush();
        }
    }

    /**
     * Sends a request and blocks for a matching reply on the same source port.
     * Not a general CSP construct (real CSP connections are more flexible) -
     * this is a pragmatic helper for simple request/reply services like ping
     * or your own custom demo port.
     *
     * @return the reply packet, or null on timeout
     */
    public CspPacket requestReply(int destination, int destinationPort, int sourcePort, int priority,
                                   byte[] payload, long timeoutMs) throws IOException {
        LinkedBlockingQueue<CspPacket> q = new LinkedBlockingQueue<>(1);
        pending.put(sourcePort, q);
        try {
            send(destination, destinationPort, sourcePort, priority, payload);
            try {
                return q.poll(timeoutMs, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        } finally {
            pending.remove(sourcePort, q);
        }
    }
}

package pl.edu.pwr.pwrinspace.poliwrocket.Model.Csp;

import java.io.ByteArrayOutputStream;
import java.util.function.Consumer;

/**
 * KISS framing (RFC-ish TNC protocol) as used by libcsp's csp_if_kiss.c.
 *
 * Frame layout on the wire:
 *   FEND [TNC_DATA] <escaped payload bytes...> FEND
 *
 * Where "payload" for a CSP/KISS link is: packed CSP header + CSP data
 * (+ CRC32 trailer, IF your libcsp build has CSP_ENABLE_CRC32 on for this
 * interface - see CspNode for the toggle).
 *
 * Verified against libcsp source (src/interfaces/csp_if_kiss.c):
 *   #define FEND      0xC0
 *   #define FESC      0xDB
 *   #define TFEND     0xDC
 *   #define TFESC     0xDD
 *   #define TNC_DATA  0x00
 */
public final class Kiss {

    public static final int FEND = 0xC0;
    public static final int FESC = 0xDB;
    public static final int TFEND = 0xDC;
    public static final int TFESC = 0xDD;
    public static final int TNC_DATA = 0x00;

    private Kiss() {
    }

    /** Wraps raw bytes (header+data[+crc]) into a full KISS frame ready to write to the serial port. */
    public static byte[] encode(byte[] raw) {
        ByteArrayOutputStream out = new ByteArrayOutputStream(raw.length + 8);
        out.write(FEND);
        out.write(TNC_DATA);
        for (byte b : raw) {
            int v = b & 0xFF;
            if (v == FEND) {
                out.write(FESC);
                out.write(TFEND);
            } else if (v == FESC) {
                out.write(FESC);
                out.write(TFESC);
            } else {
                out.write(v);
            }
        }
        out.write(FEND);
        return out.toByteArray();
    }

    /**
     * Stateful byte-by-byte decoder. Feed it bytes as they arrive from the serial
     * InputStream; it invokes the callback with the de-escaped, de-framed payload
     * (command byte stripped) whenever a complete frame is found.
     *
     * Not thread-safe - use one instance per physical link / single reader thread.
     */
    public static final class Decoder {

        private final Consumer<byte[]> onFrame;
        private final ByteArrayOutputStream buffer = new ByteArrayOutputStream(256);
        private boolean inFrame = false;
        private boolean escaping = false;
        private boolean sawCommandByte = false;

        public Decoder(Consumer<byte[]> onFrame) {
            this.onFrame = onFrame;
        }

        public void feed(byte b) {
            int v = b & 0xFF;

            if (v == FEND) {
                if (inFrame && buffer.size() > 0) {
                    onFrame.accept(buffer.toByteArray());
                }
                buffer.reset();
                inFrame = true;
                escaping = false;
                sawCommandByte = false;
                return;
            }

            if (!inFrame) {
                // Garbage before the first FEND of a session - ignore.
                return;
            }

            if (!sawCommandByte) {
                // First byte after FEND is the TNC command/port nibble byte.
                // We only handle plain data frames (0x00); anything else we still
                // consume so we don't desync, but we drop the resulting frame.
                sawCommandByte = true;
                if (v != TNC_DATA) {
                    inFrame = false; // ignore non-data KISS commands
                }
                return;
            }

            if (escaping) {
                if (v == TFEND) {
                    buffer.write(FEND);
                } else if (v == TFESC) {
                    buffer.write(FESC);
                } else {
                    // Protocol violation - pass through rather than silently drop.
                    buffer.write(v);
                }
                escaping = false;
                return;
            }

            if (v == FESC) {
                escaping = true;
                return;
            }

            buffer.write(v);
        }

        public void feed(byte[] data, int offset, int len) {
            for (int i = 0; i < len; i++) {
                feed(data[offset + i]);
            }
        }
    }
}

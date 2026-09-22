package pl.edu.pwr.pwrinspace.poliwrocket.Model.Csp;

import java.util.Arrays;
import java.util.zip.CRC32;

/**
 * A CSP packet: header + application payload.
 *
 * libcsp's KISS tx path calls csp_crc32_append(packet, false) before framing,
 * which appends a 4-byte CRC32 (standard zlib/IEEE polynomial) of
 * [header + payload] AFTER the payload, big-endian. Whether this is actually
 * active on your link depends on the CSP_ENABLE_CRC32 build option in your
 * colleague's ESP-IDF component - toggle useCrc32 to match. If frames fail
 * to decode on either side, this is the first thing to check.
 */
public final class CspPacket {

    public final CspHeader header;
    public final byte[] payload;

    public CspPacket(CspHeader header, byte[] payload) {
        this.header = header;
        this.payload = payload;
    }

    /** Builds the raw (unframed) bytes to hand to Kiss.encode(). */
    public byte[] toWireBytes(boolean useCrc32) {
        byte[] head = header.pack();
        if (!useCrc32) {
            byte[] out = new byte[head.length + payload.length];
            System.arraycopy(head, 0, out, 0, head.length);
            System.arraycopy(payload, 0, out, head.length, payload.length);
            return out;
        }

        byte[] body = new byte[head.length + payload.length];
        System.arraycopy(head, 0, body, 0, head.length);
        System.arraycopy(payload, 0, body, head.length, payload.length);

        CRC32 crc = new CRC32();
        crc.update(body);
        long crcVal = crc.getValue();

        byte[] out = new byte[body.length + 4];
        System.arraycopy(body, 0, out, 0, body.length);
        out[body.length] = (byte) ((crcVal >>> 24) & 0xFF);
        out[body.length + 1] = (byte) ((crcVal >>> 16) & 0xFF);
        out[body.length + 2] = (byte) ((crcVal >>> 8) & 0xFF);
        out[body.length + 3] = (byte) (crcVal & 0xFF);
        return out;
    }

    /**
     * Parses raw (already de-KISS-framed) bytes into a CspPacket.
     * @throws IllegalArgumentException if too short, or CRC check fails (when useCrc32=true)
     */
    public static CspPacket fromWireBytes(byte[] raw, boolean useCrc32) {
        int minLen = CspHeader.WIRE_LENGTH + (useCrc32 ? 4 : 0);
        if (raw.length < minLen) {
            throw new IllegalArgumentException("Frame too short: " + raw.length + " bytes");
        }

        int payloadEnd = useCrc32 ? raw.length - 4 : raw.length;

        if (useCrc32) {
            byte[] body = Arrays.copyOfRange(raw, 0, payloadEnd);
            CRC32 crc = new CRC32();
            crc.update(body);
            long expected = crc.getValue();
            long actual = ((long) (raw[payloadEnd] & 0xFF) << 24)
                    | ((raw[payloadEnd + 1] & 0xFF) << 16)
                    | ((raw[payloadEnd + 2] & 0xFF) << 8)
                    | (raw[payloadEnd + 3] & 0xFF);
            if (expected != actual) {
                throw new IllegalArgumentException(
                        String.format("CRC32 mismatch: expected %08X got %08X", expected, actual));
            }
        }

        CspHeader header = CspHeader.unpack(raw, 0);
        byte[] payload = Arrays.copyOfRange(raw, CspHeader.WIRE_LENGTH, payloadEnd);
        return new CspPacket(header, payload);
    }

    @Override
    public String toString() {
        return header + " payload(" + payload.length + " bytes)=" + Arrays.toString(payload);
    }
}

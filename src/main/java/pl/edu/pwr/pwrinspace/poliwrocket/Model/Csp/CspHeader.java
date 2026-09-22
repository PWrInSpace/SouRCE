package pl.edu.pwr.pwrinspace.poliwrocket.Model.Csp;

/**
 * CSP packet header (the 32-bit "id" field prepended before the payload).
 *
 * !!! VERIFY THIS AGAINST YOUR FIRMWARE BEFORE TRUSTING IT !!!
 * libcsp has shipped more than one header layout across versions (classic
 * CSP 1.x 32-bit vs. the newer CSP 2.x extended-address layout). This class
 * implements the classic CSP 1.x 32-bit layout, which is what most current
 * ESP-IDF libcsp ports still use for the on-wire "id.ext" field:
 *
 *   bit:  31 30 | 29 28 27 26 25 | 24 23 22 21 20 | 19 18 17 16 15 14 | 13 12 11 10 9 8 | 7 6 5 4 | 3 | 2 | 1 | 0
 *   field: PRI  |     SRC (5)    |     DST (5)    |    DPORT (6)     |    SPORT (6)    |  RSVD   |H|X|R|C
 *
 *   PRI   = priority (2 bits)
 *   SRC   = source address (5 bits)
 *   DST   = destination address (5 bits)
 *   DPORT = destination port (6 bits)
 *   SPORT = source port (6 bits)
 *   RSVD  = reserved (4 bits)
 *   H/X/R/C = HMAC / XTEA / RDP / CRC flags (1 bit each)
 *
 * The 32-bit id is transmitted MSB-first (network / big-endian byte order),
 * matching csp_hton32(packet->id.ext) in csp_if_kiss.c.
 *
 * If your colleague's firmware turns out to use the CSP 2.x layout instead
 * (14-bit src/dst addresses, 6-bit ports packed into a wider field), the fix
 * is localized to pack()/unpack() below - everything else in this package is
 * agnostic to the exact bit layout.
 */
public final class CspHeader {

    public final int priority;   // 0-3
    public final int source;     // 0-31
    public final int destination; // 0-31
    public final int destinationPort; // 0-63
    public final int sourcePort;      // 0-63
    public final boolean hmac;
    public final boolean xtea;
    public final boolean rdp;
    public final boolean crc;

    public CspHeader(int priority, int source, int destination,
                      int destinationPort, int sourcePort,
                      boolean hmac, boolean xtea, boolean rdp, boolean crc) {
        this.priority = priority & 0x3;
        this.source = source & 0x1F;
        this.destination = destination & 0x1F;
        this.destinationPort = destinationPort & 0x3F;
        this.sourcePort = sourcePort & 0x3F;
        this.hmac = hmac;
        this.xtea = xtea;
        this.rdp = rdp;
        this.crc = crc;
    }

    /** Convenience constructor for plain, unencrypted, non-RDP traffic. */
    public static CspHeader of(int priority, int source, int destination,
                                int destinationPort, int sourcePort) {
        return new CspHeader(priority, source, destination, destinationPort, sourcePort,
                false, false, false, false);
    }

    public byte[] pack() {
        int id = 0;
        id |= (priority & 0x3) << 30;
        id |= (source & 0x1F) << 25;
        id |= (destination & 0x1F) << 20;
        id |= (destinationPort & 0x3F) << 14;
        id |= (sourcePort & 0x3F) << 8;
        // bits 7-4 reserved, left as 0
        if (hmac) id |= (1 << 3);
        if (xtea) id |= (1 << 2);
        if (rdp)  id |= (1 << 1);
        if (crc)  id |= (1);

        return new byte[]{
                (byte) ((id >>> 24) & 0xFF),
                (byte) ((id >>> 16) & 0xFF),
                (byte) ((id >>> 8) & 0xFF),
                (byte) (id & 0xFF)
        };
    }

    public static CspHeader unpack(byte[] wire, int offset) {
        int id = ((wire[offset] & 0xFF) << 24)
                | ((wire[offset + 1] & 0xFF) << 16)
                | ((wire[offset + 2] & 0xFF) << 8)
                | (wire[offset + 3] & 0xFF);

        int priority = (id >>> 30) & 0x3;
        int source = (id >>> 25) & 0x1F;
        int destination = (id >>> 20) & 0x1F;
        int destinationPort = (id >>> 14) & 0x3F;
        int sourcePort = (id >>> 8) & 0x3F;
        boolean hmac = ((id >>> 3) & 0x1) != 0;
        boolean xtea = ((id >>> 2) & 0x1) != 0;
        boolean rdp = ((id >>> 1) & 0x1) != 0;
        boolean crc = (id & 0x1) != 0;

        return new CspHeader(priority, source, destination, destinationPort, sourcePort,
                hmac, xtea, rdp, crc);
    }

    public static final int WIRE_LENGTH = 4;

    @Override
    public String toString() {
        return String.format(
                "CspHeader{pri=%d src=%d dst=%d dport=%d sport=%d hmac=%b xtea=%b rdp=%b crc=%b}",
                priority, source, destination, destinationPort, sourcePort, hmac, xtea, rdp, crc);
    }
}

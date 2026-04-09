package pl.edu.pwr.pwrinspace.poliwrocket.Model.SerialPort;

public enum SerialPortCommand {
        CMD_UNKNOWN((byte) 0x00),
        CMD_HELP((byte) 0x01),
        CMD_SX1280_FREQ((byte) 0x02),
        CMD_SX1280_PWR((byte) 0x03),
        CMD_RESET((byte) 0x04),
        CMD_STATUS((byte) 0x05),
        CMD_SX1280_TX((byte) 0x06),
        CMD_LORA_TX((byte) 0x07),
        CMD_LOG_ON((byte) 0x08),
        CMD_LOG_OFF((byte) 0x09),
        CMD_LOG_MUTE((byte) 0x0A),
        CMD_LOG_UNMUTE((byte) 0x0B),
        CMD_COUNT((byte) 0x0C);

        private final byte code;

        SerialPortCommand(byte code) {
                this.code = code;
        }

        public byte getCode() {
                return code;
        }
}

package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Content.SimpleProtobufContent;

import java.util.List;

public class ProtobufSimpleCommand extends ProtobufBaseCommand<SimpleProtobufContent> {

    @Override
    public byte[] getCommandValueAsBytes(boolean force) {
        int device = Integer.decode(value.getLoraDevId());
        int sudo = Integer.decode(value.getSudoMask());
        int system = Integer.decode(value.getSysDevId());
        if(force) {
            return buildLoRaCommand(device | sudo, system);
        }

        return buildLoRaCommand(device, system);
    }

    @Override
    public String getListViewString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Protobuf, ");
        sb.append("command: ").append(value.getCommand()).append(", ");
        if (payload == null || payload.isEmpty()) sb.append("payload: null");
        else sb.append("payload: ").append(payload);
        return sb.toString();
    }
}

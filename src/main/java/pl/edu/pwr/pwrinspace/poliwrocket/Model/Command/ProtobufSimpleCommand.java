package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Content.SimpleProtobufContent;

import java.util.List;

public class ProtobufSimpleCommand extends ProtobufBaseCommand<SimpleProtobufContent> {

    public ProtobufSimpleCommand() {
    }

    public ProtobufSimpleCommand(String trigger, SimpleProtobufContent value, String description, List<String> destinationControllerNames, boolean isFinal, String payload) {
        super(trigger, value, description, destinationControllerNames, isFinal, payload);
    }

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
}

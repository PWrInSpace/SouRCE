package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command;

import com.fasterxml.jackson.annotation.JsonTypeName;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Command.Content.ProtobufContent;
import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;

import java.util.List;

    @JsonTypeName("ProtobufCommand")
public class ProtobufCommand extends ProtobufBaseCommand<ProtobufContent> {

    @Override
    public byte[] getCommandValueAsBytes(boolean force) {
        var protobufDevice = Configuration.getInstance().protobufDeviceRepository.getDevice(value.getDevice());
        if(force) {
            return buildLoRaCommand(
                    protobufDevice.getDeviceId() | protobufDevice.getSudoMask(),
                    Configuration.getInstance().protobufSystemRepository.getSystem(value.getSystem()).getSystemDeviceId()
            );
        }

        return buildLoRaCommand(
                protobufDevice.getDeviceId(),
                Configuration.getInstance().protobufSystemRepository.getSystem(value.getSystem()).getSystemDeviceId()
        );
    }

    public static ProtobufCommand createProtobufCommand(ProtobufContent content, boolean isFinal, String trigger, String description, String payload, CommandType commandType, List<String> destinationControllerNames) {
        ProtobufCommand command = new ProtobufCommand();
        command.setValue(content);
        command.setFinal(isFinal);
        command.setTrigger(trigger);
        command.setDescription(description);
        command.setPayload(payload);
        command.setCommandType(commandType);
        command.setDestinationControllerNames(destinationControllerNames);
        return command;
    }

    @Override
    public String getListViewString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Protobuf, ");
        sb.append("device: ").append(value.getDevice()).append(", ");
        sb.append("system: ").append(value.getSystem()).append(", ");
        sb.append("command: ").append(value.getCommand()).append(", ");
        if (payload == null || payload.isEmpty()) sb.append("payload: null");
        else sb.append("payload: ").append(payload);
        return sb.toString();
    }
}

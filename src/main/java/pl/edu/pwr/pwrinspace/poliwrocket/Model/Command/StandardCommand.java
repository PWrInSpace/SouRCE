package pl.edu.pwr.pwrinspace.poliwrocket.Model.Command;

public class StandardCommand extends Command<String> {
    @Override
    public String getListViewString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Standard, ");
        sb.append("command: ").append(value).append(", ");
        if (payload == null || payload.isEmpty()) sb.append("payload: null");
        else sb.append("payload: ").append(payload);
        return sb.toString();
    }
}

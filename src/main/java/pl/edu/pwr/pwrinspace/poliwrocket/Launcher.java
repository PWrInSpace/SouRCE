package pl.edu.pwr.pwrinspace.poliwrocket;

import pl.edu.pwr.pwrinspace.poliwrocket.Model.Configuration.Configuration;

public class Launcher {
    public static void main(String[] args) {
        if(args.length > 0 && args[0].contains("/config/")) {
            Configuration.getInstance().setConfigPath(args[0]);
        }
        Main.main(args);
    }
}

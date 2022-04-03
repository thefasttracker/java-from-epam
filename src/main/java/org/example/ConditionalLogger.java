package org.example;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConditionalLogger {
    String host = "coderanch.com";
    int port = 80;
    public void getLog() {
        Logger logger = Logger.getLogger("Status Logger");
        logger.setLevel(Level.SEVERE); // <-- 1 set log level
        Supplier<String> status = () -> {
            int timeout = 1000;
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), timeout);
                return "up";
            } catch (IOException e) {
                return "down";
            }
        };
        try {
            logger.log(Level.INFO, status); // <-- 2 works only if log level is info
            // do stuff.
            // in case of exception - log status in any log level (3)
        } catch (Exception e) {
            logger.log(Level.SEVERE, status); // <-- 3
        }
    }
}

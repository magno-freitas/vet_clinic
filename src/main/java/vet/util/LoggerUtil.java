package util;

import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import java.io.IOException;

public class LoggerUtil {
    private static final Logger LOGGER = Logger.getLogger("VetClinic");
    
    static {
        try {
            FileHandler fileHandler = new FileHandler("vetclinic.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Could not initialize logger: " + e.getMessage());
        }
    }
    
    public static Logger getLogger() {
        return LOGGER;
    }
    
    public static void logError(String message, Throwable e) {
        LOGGER.severe(message + ": " + e.getMessage());
    }
    
    public static void logInfo(String message) {
        LOGGER.info(message);
    }
    
    public static void logWarning(String message) {
        LOGGER.warning(message);
    }
}
package vet.util;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Utility class for logging
 */
public class LoggerUtil {
    private static final String LOG_FILE = "vet_clinic.log";
    private static boolean initialized = false;
    
    /**
     * Initialize the logging system
     * This should be called at application startup
     */
    public static void initialize() {
        if (!initialized) {
            try {
                // Configure the root logger
                Logger rootLogger = Logger.getLogger("");
                
                // Remove existing handlers
                for (Handler handler : rootLogger.getHandlers()) {
                    rootLogger.removeHandler(handler);
                }
                
                // Set the log level
                rootLogger.setLevel(Level.INFO);
                
                // Add console handler
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setLevel(Level.INFO);
                rootLogger.addHandler(consoleHandler);
                
                // Add file handler
                FileHandler fileHandler = new FileHandler(LOG_FILE, true);
                fileHandler.setFormatter(new SimpleFormatter());
                fileHandler.setLevel(Level.ALL);
                rootLogger.addHandler(fileHandler);
                
                initialized = true;
            } catch (IOException e) {
                System.err.println("Failed to initialize logging: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Get a logger for a class
     * @param clazz The class
     * @return The logger
     */
    public static Logger getLogger(Class<?> clazz) {
        if (!initialized) {
            initialize();
        }
        return Logger.getLogger(clazz.getName());
    }
}
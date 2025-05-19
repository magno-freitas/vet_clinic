package vet.util;

import java.io.File;
import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import vet.config.AppConfig;

/**
 * Utility class for logging
 */
public class LoggerUtil {
    private static final String DEFAULT_LOG_FILE = "logs/vet_clinic.log";
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
                
                // Get log level from configuration
                Level logLevel = getLogLevel();
                rootLogger.setLevel(logLevel);
                
                // Add console handler
                ConsoleHandler consoleHandler = new ConsoleHandler();
                consoleHandler.setLevel(logLevel);
                rootLogger.addHandler(consoleHandler);
                
                // Add file handler
                String logFile = AppConfig.getProperty("logging.file");
                if (logFile == null || logFile.trim().isEmpty()) {
                    logFile = DEFAULT_LOG_FILE;
                }
                
                // Create logs directory if it doesn't exist
                File logDir = new File(logFile).getParentFile();
                if (logDir != null && !logDir.exists()) {
                    logDir.mkdirs();
                }
                
                FileHandler fileHandler = new FileHandler(logFile, 1024 * 1024, 5, true);
                fileHandler.setFormatter(new SimpleFormatter());
                fileHandler.setLevel(Level.ALL);
                rootLogger.addHandler(fileHandler);
                
                initialized = true;
                
                Logger logger = Logger.getLogger(LoggerUtil.class.getName());
                logger.info("Logging initialized with level: " + logLevel.getName());
                logger.info("Log file: " + logFile);
                
            } catch (IOException e) {
                System.err.println("Failed to initialize logging: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Get the log level from configuration
     * @return The log level
     */
    private static Level getLogLevel() {
        String levelStr = AppConfig.getProperty("logging.level");
        if (levelStr == null) {
            return Level.INFO;
        }
        
        try {
            return Level.parse(levelStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid log level: " + levelStr + ". Using INFO.");
            return Level.INFO;
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
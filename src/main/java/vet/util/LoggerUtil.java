package vet.util;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.vetclinic.vet.service.ClientService;

/**
 * Utility class for logging
 */
public class LoggerUtil {
    public static final String WARNING = "WARNING";
    public static final String ERROR = "ERROR";
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

    public static void logError(String message, Throwable e) {
        if (!initialized) {
            initialize();
        }
        Logger logger = Logger.getLogger(LoggerUtil.class.getName());
        logger.log(Level.SEVERE, message, e);
    }

    public static void logWarning(String message) {
        if (!initialized) {
            initialize();
        }
        Logger logger = Logger.getLogger(LoggerUtil.class.getName());
        logger.warning(message);
    }

    /**
     * Custom log formatter for more readable logs
     */
    private static class CustomFormatter extends SimpleFormatter {
        private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        
        @Override
        public String format(LogRecord record) {
            StringBuilder sb = new StringBuilder();
            sb.append(dateFormat.format(new Date(record.getMillis())))
              .append(" [").append(record.getLevel()).append("] ")
              .append(record.getSourceClassName()).append(".")
              .append(record.getSourceMethodName()).append(": ")
              .append(formatMessage(record)).append("\n");
            
            if (record.getThrown() != null) {
                try {
                    Throwable t = record.getThrown();
                    sb.append("Exception: ").append(t.toString()).append("\n");
                    for (StackTraceElement element : t.getStackTrace()) {
                        sb.append("\tat ").append(element.toString()).append("\n");
                    }
                } catch (Exception ex) {
                    sb.append("Failed to format exception: ").append(ex.toString()).append("\n");
                }
            }
            
            return sb.toString();
        }
    }



    
}
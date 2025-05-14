package vet.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Configuration manager for the application
 * Loads and provides access to application properties
 */
public class AppConfig {
    private static final Logger logger = Logger.getLogger(AppConfig.class.getName());
    private static Properties properties;
    private static final String CONFIG_FILE = "application.properties";

    /**
     * Initialize the configuration
     * This should be called at application startup
     */
    public static void initialize() {
        properties = new Properties();
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
            logger.info("Configuration loaded successfully from " + CONFIG_FILE);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Could not load configuration file: " + e.getMessage(), e);
            // Load default values as fallback
            setDefaultProperties();
        }
    }

    /**
     * Set default properties in case the config file cannot be loaded
     */
    private static void setDefaultProperties() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/vet_clinic");
        properties.setProperty("db.user", "root");
        properties.setProperty("db.password", "");
        properties.setProperty("mail.smtp.host", "smtp.example.com");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.username", "user@example.com");
        properties.setProperty("mail.password", "password");
        logger.info("Using default configuration values");
    }

    /**
     * Get a property value by key
     * @param key The property key
     * @return The property value or null if not found
     */
    public static String getProperty(String key) {
        if (properties == null) {
            initialize();
        }
        return properties.getProperty(key);
    }

    /**
     * Get the database URL
     * @return The database URL
     */
    public static String getDatabaseUrl() {
        if (properties == null) {
            initialize();
        }
        return properties.getProperty("db.url");
    }

    /**
     * Get the database username
     * @return The database username
     */
    public static String getDatabaseUser() {
        if (properties == null) {
            initialize();
        }
        return properties.getProperty("db.user");
    }

    /**
     * Get the database password
     * @return The database password
     */
    public static String getDatabasePassword() {
        if (properties == null) {
            initialize();
        }
        return properties.getProperty("db.password");
    }

    /**
     * Get the SMTP host for email
     * @return The SMTP host
     */
    public static String getSmtpHost() {
        if (properties == null) {
            initialize();
        }
        return properties.getProperty("mail.smtp.host");
    }

    /**
     * Get the SMTP port for email
     * @return The SMTP port
     */
    public static String getSmtpPort() {
        if (properties == null) {
            initialize();
        }
        return properties.getProperty("mail.smtp.port");
    }

    /**
     * Get the email username
     * @return The email username
     */
    public static String getEmailUsername() {
        if (properties == null) {
            initialize();
        }
        return properties.getProperty("mail.username");
    }

    /**
     * Get the email password
     * @return The email password
     */
    public static String getEmailPassword() {
        if (properties == null) {
            initialize();
        }
        return properties.getProperty("mail.password");
    }
}
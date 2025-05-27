package vet.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import vet.util.LoggerUtil;

/**
 * Configuration manager for the application
 * Loads and provides access to application properties
 */
public class AppConfig {
    private static final Logger logger = LoggerUtil.getLogger(AppConfig.class);
    private static Properties properties;
    private static final String CONFIG_FILE = "src/main/resources/application.properties";

    /**
     * Initialize the configuration
     * This should be called at application startup
     */
    public static void initialize() {
        properties = new Properties();
        try (InputStream input = AppConfig.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input != null) {
                properties.load(input);
                logger.info("Configuration loaded successfully from classpath");
            } else {
                // Try loading from file system if not found in classpath
                try (FileInputStream fileInput = new FileInputStream(CONFIG_FILE)) {
                    properties.load(fileInput);
                    logger.info("Configuration loaded successfully from " + CONFIG_FILE);
                } catch (IOException e) {
                    logger.log(Level.WARNING, "Could not load configuration file from file system: " + e.getMessage());
                    setDefaultProperties();
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not load configuration from classpath: " + e.getMessage());
            setDefaultProperties();
        }
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
public static Properties getProperties() {
    if (properties == null) {
        initialize();
    }
    return properties;
}
    /**
     * Get the database username
     * @return The database username
     */
    public static String getDatabaseUser() {
        if (properties == null) {
            initialize();
        }
        return properties.getProperty("db.username");
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
     * Get the database connection pool size
     * @return The database connection pool size
     */
    public static int getDatabasePoolSize() {
        if (properties == null) {
            initialize();
        }
        try {
            return Integer.parseInt(properties.getProperty("db.poolSize", "10"));
        } catch (NumberFormatException e) {
            logger.warning("Invalid pool size in configuration, using default: 10");
            return 10;
        }
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
     * Get the email authentication setting
     * @return The email authentication setting
     */
    public static boolean getSmtpAuth() {
        if (properties == null) {
            initialize();
        }
        return Boolean.parseBoolean(properties.getProperty("mail.smtp.auth", "true"));
    }

    /**
     * Get the email STARTTLS setting
     * @return The email STARTTLS setting
     */
    public static boolean getSmtpStartTls() {
        if (properties == null) {
            initialize();
        }
        return Boolean.parseBoolean(properties.getProperty("mail.smtp.starttls.enable", "true"));
    }


    /**
     * Set default properties in case the config file cannot be loaded
     */
    private static void setDefaultProperties() {
        properties.setProperty("db.url", "jdbc:mysql://localhost:3306/vet_clinic");
        properties.setProperty("db.username", "root");
        properties.setProperty("db.password", "root");
        properties.setProperty("db.poolSize", "10");
        properties.setProperty("mail.smtp.host", "smtp.gmail.com");
        properties.setProperty("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.auth", "true");
        properties.setProperty("mail.smtp.starttls.enable", "true");
        logger.info("Using default configuration values");
    }
}
package vet.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class AppConfig {
    private static Properties properties;
    private static final String CONFIG_FILE = "application.properties";

    static {
        properties = new Properties();
        try (FileInputStream input = new FileInputStream(CONFIG_FILE)) {
            properties.load(input);
        } catch (IOException e) {
            System.err.println("Could not load configuration file: " + e.getMessage());
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String getDatabaseUrl() {
        return properties.getProperty("db.url");
    }

    public static String getDatabaseUser() {
        return properties.getProperty("db.user");
    }

    public static String getDatabasePassword() {
        return properties.getProperty("db.password");
    }

    public static String getSmtpHost() {
        return properties.getProperty("mail.smtp.host");
    }

    public static String getSmtpPort() {
        return properties.getProperty("mail.smtp.port");
    }

    public static String getEmailUsername() {
        return properties.getProperty("mail.username");
    }

    public static String getEmailPassword() {
        return properties.getProperty("mail.password");
    }
}
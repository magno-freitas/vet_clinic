package vet;

import vet.config.AppConfig;
import vet.ui.MainFrame;
import vet.util.ConnectionPool;
import vet.util.LoggerUtil;

import javax.swing.SwingUtilities;
import java.util.logging.Logger;

/**
 * Main entry point for the Veterinary Clinic Application
 */
public class Main {
    private static final Logger logger = LoggerUtil.getLogger(Main.class);

    public static void main(String[] args) {
        try {
            // Initialize configuration
            AppConfig.initialize();
            logger.info("Application configuration loaded successfully");
            
            // Initialize database connection pool
            ConnectionPool.initialize();
            logger.info("Database connection pool initialized");
            
            // Start the UI
            SwingUtilities.invokeLater(() -> {
                try {
                    MainFrame mainFrame = new MainFrame();
                    mainFrame.setVisible(true);
                    logger.info("Application UI started successfully");
                } catch (Exception e) {
                    logger.severe("Error starting application UI: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            
            // Add shutdown hook to clean up resources
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                ConnectionPool.closePool();
                logger.info("Application shutting down, resources cleaned up");
            }));
            
        } catch (Exception e) {
            logger.severe("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
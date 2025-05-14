package vet.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import vet.config.AppConfig;
import vet.exception.DatabaseException;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Database connection pool using HikariCP
 */
public class ConnectionPool {
    private static final Logger logger = LoggerUtil.getLogger(ConnectionPool.class);
    private static HikariDataSource dataSource;
    
    /**
     * Initialize the connection pool
     * This should be called at application startup
     */
    public static void initialize() {
        try {
            if (dataSource == null || dataSource.isClosed()) {
                logger.info("Initializing database connection pool");
                
                HikariConfig config = new HikariConfig();
                config.setJdbcUrl(AppConfig.getDatabaseUrl());
                config.setUsername(AppConfig.getDatabaseUser());
                config.setPassword(AppConfig.getDatabasePassword());
                config.setMaximumPoolSize(10);
                config.setMinimumIdle(5);
                config.setIdleTimeout(300000); // 5 minutes
                config.setConnectionTimeout(20000); // 20 seconds
                config.setPoolName("VetClinicPool");
                
                // Connection testing
                config.setConnectionTestQuery("SELECT 1");
                config.setValidationTimeout(5000); // 5 seconds
                
                // Add useful metrics
                config.addDataSourceProperty("cachePrepStmts", "true");
                config.addDataSourceProperty("prepStmtCacheSize", "250");
                config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
                config.addDataSourceProperty("useServerPrepStmts", "true");
                
                dataSource = new HikariDataSource(config);
                logger.info("Database connection pool initialized successfully");
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to initialize connection pool", e);
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }
    
    /**
     * Get a connection from the pool
     * @return A database connection
     * @throws DatabaseException If there is an error getting a connection
     */
    public static Connection getConnection() throws DatabaseException {
        try {
            if (dataSource == null || dataSource.isClosed()) {
                initialize();
            }
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Failed to get database connection", e);
            throw new DatabaseException("Failed to get database connection: " + e.getMessage(), e);
        }
    }
    
    /**
     * Close the connection pool
     * This should be called when the application is shutting down
     */
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Closing database connection pool");
            dataSource.close();
            logger.info("Database connection pool closed");
        }
    }
}
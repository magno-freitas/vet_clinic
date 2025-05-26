
package vet.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;
import vet.config.AppConfig;

public class ConnectionPool {
    private static final Logger logger = LoggerUtil.getLogger(ConnectionPool.class);
    private static HikariDataSource dataSource;
    
    static {
        try {
            Properties props = AppConfig.getProperties();
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(props.getProperty("db.url"));
            config.setUsername(props.getProperty("db.username"));
            config.setPassword(props.getProperty("db.password"));
            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.poolSize", "10")));
            config.setMinimumIdle(5);
            config.setIdleTimeout(300000);
            config.setConnectionTimeout(20000);
            
            // Additional recommended settings
            config.setAutoCommit(true);
            config.setPoolName("VetClinicPool");
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            
            dataSource = new HikariDataSource(config);
            logger.info("Connection pool initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize connection pool", e);
            throw new ExceptionInInitializerError(e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("Connection pool not initialized");
        }
        return dataSource.getConnection();
    }
    
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            logger.info("Connection pool closed");
        }
    }

    public static void initialize() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'initialize'");
    }
}
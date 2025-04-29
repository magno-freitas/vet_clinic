package vet.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import vet.config.AppConfig;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnectionPool {
    private static HikariDataSource dataSource;
    
    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(AppConfig.getDatabaseUrl());
        config.setUsername(AppConfig.getDatabaseUser());
        config.setPassword(AppConfig.getDatabasePassword());
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(20000);
        
        dataSource = new HikariDataSource(config);
    }
    
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
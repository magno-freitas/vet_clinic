package vet;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.Level;

public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    
    // Use environment variables for security
    private static final String URL = System.getenv("DB_URL") != null ? 
        System.getenv("DB_URL") : "jdbc:mysql://localhost:3306/vet_clinic";
    private static final String USER = System.getenv("DB_USER") != null ? 
        System.getenv("DB_USER") : "root";
    private static final String PASSWORD = System.getenv("DB_PASSWORD") != null ? 
        System.getenv("DB_PASSWORD") : "";

    private static Connection connection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "MySQL Driver not found", e);
        }
    }

    public static Connection getConnection() throws SQLException {
<<<<<<< HEAD
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error connecting to database", e);
                throw e;
            }
        }
        return connection;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.log(Level.SEVERE, "Error closing database connection", e);
            }
=======
        try {
            Connection conn = DriverManager.getConnection(
                AppConfig.getDatabaseUrl(),
                AppConfig.getDatabaseUser(),
                AppConfig.getDatabasePassword());
            conn.setAutoCommit(true);
            return conn;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Erro ao conectar ao banco de dados", e);
            throw e;
>>>>>>> 918ce33a363183d1ca964e2773ed218aa7f6e9b0
        }
    }
}

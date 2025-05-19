package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.AuditLog;
import util.ConnectionPool;
import util.LoggerUtil;
import exception.DatabaseException;

/**
 * Service class for managing audit logs
 */
public class AuditLogService {
    private static final Logger logger = LoggerUtil.getLogger(AuditLogService.class);
    
    /**
     * Log an action
     * @param action The action description
     * @param username The username of the user who performed the action
     * @throws DatabaseException If there is a database error
     */
    public void logAction(String action, String username) throws DatabaseException {
        String query = "INSERT INTO audit_log (action, username, timestamp) VALUES (?, ?, ?)";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, action);
            stmt.setString(2, username);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            
            stmt.executeUpdate();
            
            logger.info("Logged action: " + action + " by user: " + username);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error logging action", e);
            throw new DatabaseException("Error logging action: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get audit logs for a specific user
     * @param username The username
     * @return List of audit logs for the user
     * @throws DatabaseException If there is a database error
     */
    public List<AuditLog> getLogsByUser(String username) throws DatabaseException {
        String query = "SELECT * FROM audit_log WHERE username = ? ORDER BY timestamp DESC";
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToAuditLog(rs));
                }
            }
            
            logger.info("Retrieved " + logs.size() + " audit logs for user: " + username);
            return logs;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving audit logs for user", e);
            throw new DatabaseException("Error retrieving audit logs: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get all audit logs
     * @return List of all audit logs
     * @throws DatabaseException If there is a database error
     */
    public List<AuditLog> getAllLogs() throws DatabaseException {
        String query = "SELECT * FROM audit_log ORDER BY timestamp DESC";
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                logs.add(mapResultSetToAuditLog(rs));
            }
            
            logger.info("Retrieved " + logs.size() + " audit logs");
            return logs;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving audit logs", e);
            throw new DatabaseException("Error retrieving audit logs: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get audit logs for a specific date range
     * @param startDate The start date
     * @param endDate The end date
     * @return List of audit logs in the date range
     * @throws DatabaseException If there is a database error
     */
    public List<AuditLog> getLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) throws DatabaseException {
        String query = "SELECT * FROM audit_log WHERE timestamp BETWEEN ? AND ? ORDER BY timestamp DESC";
        List<AuditLog> logs = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setTimestamp(1, Timestamp.valueOf(startDate));
            stmt.setTimestamp(2, Timestamp.valueOf(endDate));
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToAuditLog(rs));
                }
            }
            
            logger.info("Retrieved " + logs.size() + " audit logs for date range");
            return logs;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving audit logs for date range", e);
            throw new DatabaseException("Error retrieving audit logs: " + e.getMessage(), e);
        }
    }
    
    /**
     * Map a ResultSet to an AuditLog object
     * @param rs The ResultSet
     * @return The AuditLog object
     * @throws SQLException If there is a database error
     */
    private AuditLog mapResultSetToAuditLog(ResultSet rs) throws SQLException {
        AuditLog log = new AuditLog();
        log.setLogId(rs.getInt("log_id"));
        log.setAction(rs.getString("action"));
        log.setUsername(rs.getString("username"));
        log.setTimestamp(rs.getTimestamp("timestamp").toLocalDateTime());
        return log;
    }
}
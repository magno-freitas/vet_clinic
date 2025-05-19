package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents an audit log entry in the system
 */
public class AuditLog {
    private int logId;
    private String action;
    private String username;
    private LocalDateTime timestamp;
    
    /**
     * Default constructor
     */
    public AuditLog() {
    }
    
    /**
     * Constructor with essential fields
     * @param action The action description
     * @param username The username of the user who performed the action
     * @param timestamp The timestamp of the action
     */
    public AuditLog(String action, String username, LocalDateTime timestamp) {
        this.action = action;
        this.username = username;
        this.timestamp = timestamp;
    }
    
    /**
     * Get the log ID
     * @return The log ID
     */
    public int getLogId() {
        return logId;
    }
    
    /**
     * Set the log ID
     * @param logId The log ID
     */
    public void setLogId(int logId) {
        this.logId = logId;
    }
    
    /**
     * Get the action description
     * @return The action description
     */
    public String getAction() {
        return action;
    }
    
    /**
     * Set the action description
     * @param action The action description
     */
    public void setAction(String action) {
        this.action = action;
    }
    
    /**
     * Get the username
     * @return The username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Set the username
     * @param username The username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Get the timestamp
     * @return The timestamp
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    
    /**
     * Set the timestamp
     * @param timestamp The timestamp
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    
    /**
     * Get the formatted timestamp
     * @return The formatted timestamp
     */
    public String getFormattedTimestamp() {
        if (timestamp == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        return timestamp.format(formatter);
    }
    
    @Override
    public String toString() {
        return "AuditLog{" +
                "logId=" + logId +
                ", action='" + action + '\'' +
                ", username='" + username + '\'' +
                ", timestamp=" + getFormattedTimestamp() +
                '}';
    }
}
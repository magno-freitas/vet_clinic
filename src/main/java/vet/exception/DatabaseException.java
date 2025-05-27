package vet.exception;

import java.sql.SQLException;

/**
 * Exception thrown when database operations fail
 */
public class DatabaseException extends VetClinicException {
    
    /**
     * Create a new DatabaseException with a message
     * @param message The error message
     */
    public DatabaseException(String message) {
        super(message);
    }
    
    /**
     * Create a new DatabaseException with a message and cause
     * @param message The error message
     * @param cause The cause of the exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Create a new DatabaseException from an SQLException
     * @param e The SQLException
     * @return A new DatabaseException
     */
    public static DatabaseException fromSQLException(SQLException e) {
        return new DatabaseException("Database error: " + e.getMessage(), e);
    }
    
    /**
     * Create a new DatabaseException from an SQLException with a custom message
     * @param message The custom message
     * @param e The SQLException
     * @return A new DatabaseException
     */
    public static DatabaseException fromSQLException(String message, SQLException e) {
        return new DatabaseException(message + ": " + e.getMessage(), e);
    }
}
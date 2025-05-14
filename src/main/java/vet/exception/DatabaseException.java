package vet.exception;

/**
 * Exception thrown when there is a database error
 */
public class DatabaseException extends VetClinicException {
    
    /**
     * Constructor
     * @param message The error message
     */
    public DatabaseException(String message) {
        super(message);
    }
    
    /**
     * Constructor
     * @param message The error message
     * @param cause The cause of the exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
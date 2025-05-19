package vet.exception;

/**
 * Exception thrown when there is a security-related error
 */
public class SecurityException extends VetClinicException {
    
    /**
     * Constructor
     * @param message The error message
     */
    public SecurityException(String message) {
        super(message);
    }
    
    /**
     * Constructor
     * @param message The error message
     * @param cause The cause of the exception
     */
    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }
}
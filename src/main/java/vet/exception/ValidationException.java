package vet.exception;

/**
 * Exception thrown when there is a validation error
 */
public class ValidationException extends VetClinicException {
    
    /**
     * Constructor
     * @param message The error message
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Constructor
     * @param message The error message
     * @param cause The cause of the exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
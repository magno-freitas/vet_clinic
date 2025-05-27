package vet.exception;

/**
 * Exception thrown when validation fails
 */
public class ValidationException extends VetClinicException {
    
    /**
     * Create a new ValidationException with a message
     * @param message The error message
     */
    public ValidationException(String message) {
        super(message);
    }
    
    /**
     * Create a new ValidationException with a message and cause
     * @param message The error message
     * @param cause The cause of the exception
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
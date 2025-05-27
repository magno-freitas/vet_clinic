package vet.exception;

/**
 * Base exception class for all application exceptions
 */
public class VetClinicException extends RuntimeException {
    
    /**
     * Create a new VetClinicException with a message
     * @param message The error message
     */
    public VetClinicException(String message) {
        super(message);
    }

    /**
     * Create a new VetClinicException with a message and cause
     * @param message The error message
     * @param cause The cause of the exception
     */
    public VetClinicException(String message, Throwable cause) {
        super(message, cause);
    }
    
    /**
     * Create a new VetClinicException with a cause
     * @param cause The cause of the exception
     */
    public VetClinicException(Throwable cause) {
        super(cause);
    }
}
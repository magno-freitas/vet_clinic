package vet.exception;

/**
 * Base exception class for all veterinary clinic exceptions
 */
public class VetClinicException extends Exception {
    
    /**
     * Constructor
     * @param message The error message
     */
    public VetClinicException(String message) {
        super(message);
    }
    
    /**
     * Constructor
     * @param message The error message
     * @param cause The cause of the exception
     */
    public VetClinicException(String message, Throwable cause) {
        super(message, cause);
    }
}
package exception;



public class VetClinicException extends RuntimeException {
    public VetClinicException(String message) {
        super(message);
    }

    public VetClinicException(String message, Throwable cause) {
        super(message, cause);
    }
}
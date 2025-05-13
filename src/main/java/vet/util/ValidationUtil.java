package util;

import exception.VetClinicException;
import java.util.regex.Pattern;
import java.sql.Timestamp;
import java.util.Date;

public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^\\+?[1-9][0-9]{7,14}$");
    
    public static void validateEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email).matches()) {
            throw new VetClinicException("Invalid email format: " + email);
        }
    }
    
    public static void validatePhone(String phone) {
        if (phone == null || !PHONE_PATTERN.matcher(phone).matches()) {
            throw new VetClinicException("Invalid phone number format: " + phone);
        }
    }
    
    public static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new VetClinicException(fieldName + " cannot be null");
        }
    }
    
    public static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new VetClinicException(fieldName + " cannot be empty");
        }
    }
    
    public static void validateFutureDate(Date date) {
        if (date == null || date.before(new Date())) {
            throw new VetClinicException("Date must be in the future");
        }
    }
    
    public static void validateBusinessHours(Timestamp timestamp) {
        if (timestamp == null) {
            throw new VetClinicException("Timestamp cannot be null");
        }
        // Add business hours validation logic here
    }
}
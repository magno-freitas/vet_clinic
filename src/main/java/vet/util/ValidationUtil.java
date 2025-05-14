package vet.util;

import vet.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtil {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\+?[0-9]{10,15}$");
    
    /**
     * Validate that a string is not null or empty
     * @param value The string to validate
     * @param fieldName The name of the field
     * @throws ValidationException If the string is null or empty
     */
    public static void validateRequired(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " is required");
        }
    }
    
    /**
     * Validate that a string has a minimum length
     * @param value The string to validate
     * @param minLength The minimum length
     * @param fieldName The name of the field
     * @throws ValidationException If the string is shorter than the minimum length
     */
    public static void validateMinLength(String value, int minLength, String fieldName) throws ValidationException {
        if (value != null && value.length() < minLength) {
            throw new ValidationException(fieldName + " must be at least " + minLength + " characters");
        }
    }
    
    /**
     * Validate that a string has a maximum length
     * @param value The string to validate
     * @param maxLength The maximum length
     * @param fieldName The name of the field
     * @throws ValidationException If the string is longer than the maximum length
     */
    public static void validateMaxLength(String value, int maxLength, String fieldName) throws ValidationException {
        if (value != null && value.length() > maxLength) {
            throw new ValidationException(fieldName + " must be at most " + maxLength + " characters");
        }
    }
    
    /**
     * Validate that a value is positive
     * @param value The value to validate
     * @param fieldName The name of the field
     * @throws ValidationException If the value is not positive
     */
    public static void validatePositive(double value, String fieldName) throws ValidationException {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be positive");
        }
    }
    
    /**
     * Validate that a value is not negative
     * @param value The value to validate
     * @param fieldName The name of the field
     * @throws ValidationException If the value is negative
     */
    public static void validateNonNegative(double value, String fieldName) throws ValidationException {
        if (value < 0) {
            throw new ValidationException(fieldName + " cannot be negative");
        }
    }
    
    /**
     * Validate that a string is a valid email address
     * @param email The email address to validate
     * @throws ValidationException If the email address is invalid
     */
    public static void validateEmail(String email) throws ValidationException {
        validateRequired(email, "Email");
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new ValidationException("Invalid email address");
        }
    }
    
    /**
     * Validate that a string is a valid phone number
     * @param phone The phone number to validate
     * @throws ValidationException If the phone number is invalid
     */
    public static void validatePhone(String phone) throws ValidationException {
        validateRequired(phone, "Phone");
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new ValidationException("Invalid phone number");
        }
    }
    
    /**
     * Sanitize a string to prevent SQL injection
     * @param input The string to sanitize
     * @return The sanitized string
     */
    public static String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }
        // Remove potentially dangerous characters
        return input.replaceAll("[;'\"]", "");
    }
}
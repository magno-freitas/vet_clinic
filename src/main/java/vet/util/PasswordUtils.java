package vet.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Utility class for password hashing and verification
 */
public class PasswordUtils {
    private static final Logger logger = LoggerUtil.getLogger(PasswordUtils.class);
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;
    private static final String ALGORITHM = "PBKDF2WithHmacSHA256";
    
    /**
     * Hash a password
     * @param password The password to hash
     * @return The hashed password
     */
    public static String hashPassword(String password) {
        try {
            // Generate a random salt
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH];
            random.nextBytes(salt);
            
            // Hash the password
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] hash = factory.generateSecret(spec).getEncoded();
            
            // Format: iterations:salt:hash
            return ITERATIONS + ":" + Base64.getEncoder().encodeToString(salt) + ":" + Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            logger.log(Level.SEVERE, "Error hashing password", e);
            throw new RuntimeException("Error hashing password", e);
        }
    }
    
    /**
     * Verify a password against a hash
     * @param password The password to verify
     * @param storedHash The stored hash
     * @return True if the password matches the hash, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash) {
        try {
            // Split the stored hash into its parts
            String[] parts = storedHash.split(":");
            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hash = Base64.getDecoder().decode(parts[2]);
            
            // Hash the password with the same salt and iterations
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, hash.length * 8);
            SecretKeyFactory factory = SecretKeyFactory.getInstance(ALGORITHM);
            byte[] testHash = factory.generateSecret(spec).getEncoded();
            
            // Compare the hashes
            int diff = hash.length ^ testHash.length;
            for (int i = 0; i < hash.length && i < testHash.length; i++) {
                diff |= hash[i] ^ testHash[i];
            }
            return diff == 0;
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | ArrayIndexOutOfBoundsException | NumberFormatException e) {
            logger.log(Level.SEVERE, "Error verifying password", e);
            return false;
        }
    }
    
    /**
     * Generate a random password
     * @param length The length of the password
     * @return The generated password
     */
    public static String generateRandomPassword(int length) {
        if (length < 12) {
            length = 12; // Increased minimum length for better security
        }
        
        String upperChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowerChars = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String specialChars = "!@#$%^&*()_+-=[]{}|;:,.<>?";
        
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        
        // Ensure at least one character from each category
        sb.append(upperChars.charAt(random.nextInt(upperChars.length())));
        sb.append(lowerChars.charAt(random.nextInt(lowerChars.length())));
        sb.append(numbers.charAt(random.nextInt(numbers.length())));
        sb.append(specialChars.charAt(random.nextInt(specialChars.length())));
        
        // Fill the rest of the password
        String allChars = upperChars + lowerChars + numbers + specialChars;
        for (int i = 4; i < length; i++) {
            int randomIndex = random.nextInt(allChars.length());
            sb.append(allChars.charAt(randomIndex));
        }
        
        // Shuffle the password
        char[] password = sb.toString().toCharArray();
        for (int i = 0; i < password.length; i++) {
            int j = random.nextInt(password.length);
            char temp = password[i];
            password[i] = password[j];
            password[j] = temp;
        }
        
        return new String(password);
    }
}
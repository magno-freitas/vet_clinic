package vet.model;

/**
 * Represents a user in the veterinary clinic system
 */
public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private UserRole role;
    private String email;
    private String fullName;
    private boolean active;

    /**
     * Default constructor
     */
    public User() {
        this.active = true;
    }

    /**
     * Constructor with essential fields
     * @param username The username
     * @param passwordHash The password hash
     * @param role The user role
     */
    public User(String username, String passwordHash, UserRole role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
        this.active = true;
    }

    /**
     * Get the user ID
     * @return The user ID
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Set the user ID
     * @param userId The user ID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Get the username
     * @return The username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username
     * @param username The username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Get the password hash
     * @return The password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Set the password hash
     * @param passwordHash The password hash
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /**
     * Get the user role
     * @return The user role
     */
    public UserRole getRole() {
        return role;
    }

    /**
     * Set the user role
     * @param role The user role
     */
    public void setRole(UserRole role) {
        this.role = role;
    }

    /**
     * Get the email
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the email
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the full name
     * @return The full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Set the full name
     * @param fullName The full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    /**
     * Check if the user is active
     * @return true if the user is active, false otherwise
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set whether the user is active
     * @param active true if the user is active, false otherwise
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", role=" + role +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", active=" + active +
                '}';
    }

    public String getPassword() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPassword'");
    }
}
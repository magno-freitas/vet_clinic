package model;

/**
 * Enum representing user roles in the veterinary clinic system
 */
public enum UserRole {
    ADMIN("Administrator"),
    VETERINARIAN("Veterinarian"),
    RECEPTIONIST("Receptionist"),
    ASSISTANT("Assistant"),
    CLIENT("Client");
    
    private final String description;
    
    /**
     * Constructor
     * @param description The role description
     */
    UserRole(String description) {
        this.description = description;
    }
    
    /**
     * Get the role description
     * @return The role description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get a UserRole by its description
     * @param description The description to search for
     * @return The matching UserRole or null if not found
     */
    public static UserRole fromDescription(String description) {
        for (UserRole role : values()) {
            if (role.getDescription().equalsIgnoreCase(description)) {
                return role;
            }
        }
        return null;
    }
}
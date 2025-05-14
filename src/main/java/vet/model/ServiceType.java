package model;

/**
 * Enum representing the types of services offered by the veterinary clinic
 */
public enum ServiceType {
    BATH("Bath", 50.0, 60),
    GROOMING("Grooming", 70.0, 90),
    VACCINATION("Vaccination", 100.0, 30),
    CONSULTATION("Consultation", 150.0, 60),
    SURGERY("Surgery", 500.0, 120),
    DENTAL_CLEANING("Dental Cleaning", 200.0, 60),
    EMERGENCY("Emergency", 250.0, 90);
    
    private final String description;
    private final double defaultPrice;
    private final int durationMinutes;
    
    /**
     * Constructor
     * @param description The service description
     * @param defaultPrice The default price
     * @param durationMinutes The duration in minutes
     */
    ServiceType(String description, double defaultPrice, int durationMinutes) {
        this.description = description;
        this.defaultPrice = defaultPrice;
        this.durationMinutes = durationMinutes;
    }
    
    /**
     * Get the service description
     * @return The service description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Get the default price
     * @return The default price
     */
    public double getDefaultPrice() {
        return defaultPrice;
    }
    
    /**
     * Get the duration in minutes
     * @return The duration in minutes
     */
    public int getDurationMinutes() {
        return durationMinutes;
    }
    
    /**
     * Get a ServiceType by its description
     * @param description The description to search for
     * @return The matching ServiceType or null if not found
     */
    public static ServiceType fromDescription(String description) {
        for (ServiceType type : values()) {
            if (type.getDescription().equalsIgnoreCase(description)) {
                return type;
            }
        }
        return null;
    }
}
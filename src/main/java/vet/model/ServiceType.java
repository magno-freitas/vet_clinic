package vet.model;

/**
 * Enum representing the types of services offered by the veterinary clinic
 */
public enum ServiceType {
    // Grooming services
    BANHO("Banho", 50.0, 60),
    TOSA("Tosa", 70.0, 90),
    
    // Medical services
    VACCINATION("Vacina", 100.0, 30),
    CONSULTATION("Consulta", 150.0, 60),
    SURGERY("Cirurgia", 500.0, 120),
    DENTAL_CLEANING("Limpeza Dental", 200.0, 60),
    EMERGENCY("Emergência", 250.0, 90),
    
    // Additional services
    CHECKUP("Check-up", 120.0, 45),
    MICROCHIP("Microchip", 80.0, 15),
    DEWORMING("Vermifugação", 60.0, 20),
    XRAY("Raio-X", 180.0, 30),
    ULTRASOUND("Ultrassom", 200.0, 45),
    LABORATORY("Exames Laboratoriais", 150.0, 30);
    
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
    
    /**
     * For backward compatibility with old code using VACINA
     * @return The matching ServiceType
     */
    public static ServiceType valueOf(String name, boolean compatibilityMode) {
        if (compatibilityMode && "VACINA".equals(name)) {
            return VACCINATION;
        }
        return ServiceType.valueOf(name);
    }
}
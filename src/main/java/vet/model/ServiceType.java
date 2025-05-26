package vet.model;

/**
 * Enum representing the types of services offered by the veterinary clinic
 */
public enum ServiceType {
    BANHO("Banho", 50.0, 60),
    TOSA("Tosa", 70.0, 90),
    VACINA("Vacina", 100.0, 30),
    CONSULTA("Consulta", 150.0, 60),
    CIRURGIA("Cirurgia", 500.0, 120),
    LIMPEZA_DENTAL("Limpeza Dental", 200.0, 60),
    EMERGENCIA("Emergência", 250.0, 90);
    
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
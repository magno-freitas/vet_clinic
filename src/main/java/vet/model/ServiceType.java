package vet.model;

/**
 * Enum representing the types of services offered by the veterinary clinic
 */
public enum ServiceType {
    VACCINATION("Vacinação", 100.0, 30),
    CONSULTATION("Consulta", 150.0, 60),
    SURGERY("Cirurgia", 500.0, 120),
    EMERGENCY("Emergência", 250.0, 90),
    DENTAL_CLEANING("Limpeza Dental", 200.0, 60),
    GROOMING_BATH("Banho", 80.0, 45),
    GROOMING_TRIM("Tosa", 100.0, 60), VACCINE, TOSA, BANHO, CONSULTA, VACINA;

    private final String description;
    private final double defaultPrice;
    private final int durationMinutes;

    ServiceType(String description, double defaultPrice, int durationMinutes) {
        this.description = description;
        this.defaultPrice = defaultPrice;
        this.durationMinutes = durationMinutes;
    }

    public String getDescription() {
        return description;
    }

    public double getDefaultPrice() {
        return defaultPrice;
    }

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

    public String getDescricao() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDescricao'");
    }
}
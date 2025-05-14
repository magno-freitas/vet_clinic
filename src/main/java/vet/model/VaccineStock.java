package model;

import java.util.Date;

/**
 * Represents vaccine stock in the veterinary clinic
 */
public class VaccineStock {
    private int vaccineId;
    private String vaccineName;
    private int quantity;
    private int reorderLevel;
    private Date expirationDate;
    private double unitPrice;
    private String manufacturer;
    private String batchNumber;

    /**
     * Default constructor
     */
    public VaccineStock() {
    }

    /**
     * Constructor with essential fields
     * @param vaccineName The name of the vaccine
     * @param quantity The quantity in stock
     * @param reorderLevel The reorder level
     */
    public VaccineStock(String vaccineName, int quantity, int reorderLevel) {
        this.vaccineName = vaccineName;
        this.quantity = quantity;
        this.reorderLevel = reorderLevel;
    }

    /**
     * Get the vaccine ID
     * @return The vaccine ID
     */
    public int getVaccineId() {
        return vaccineId;
    }

    /**
     * Set the vaccine ID
     * @param vaccineId The vaccine ID
     */
    public void setVaccineId(int vaccineId) {
        this.vaccineId = vaccineId;
    }

    /**
     * Get the vaccine name
     * @return The vaccine name
     */
    public String getVaccineName() {
        return vaccineName;
    }

    /**
     * Set the vaccine name
     * @param vaccineName The vaccine name
     */
    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    /**
     * Get the quantity
     * @return The quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Set the quantity
     * @param quantity The quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Get the reorder level
     * @return The reorder level
     */
    public int getReorderLevel() {
        return reorderLevel;
    }

    /**
     * Set the reorder level
     * @param reorderLevel The reorder level
     */
    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }

    /**
     * Get the expiration date
     * @return The expiration date
     */
    public Date getExpirationDate() {
        return expirationDate;
    }

    /**
     * Set the expiration date
     * @param expirationDate The expiration date
     */
    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }

    /**
     * Get the unit price
     * @return The unit price
     */
    public double getUnitPrice() {
        return unitPrice;
    }

    /**
     * Set the unit price
     * @param unitPrice The unit price
     */
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    /**
     * Get the manufacturer
     * @return The manufacturer
     */
    public String getManufacturer() {
        return manufacturer;
    }

    /**
     * Set the manufacturer
     * @param manufacturer The manufacturer
     */
    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    /**
     * Get the batch number
     * @return The batch number
     */
    public String getBatchNumber() {
        return batchNumber;
    }

    /**
     * Set the batch number
     * @param batchNumber The batch number
     */
    public void setBatchNumber(String batchNumber) {
        this.batchNumber = batchNumber;
    }
    
    /**
     * Check if the stock is low
     * @return true if the stock is low, false otherwise
     */
    public boolean isLowStock() {
        return quantity <= reorderLevel;
    }
    
    /**
     * Check if the vaccine is expired
     * @return true if the vaccine is expired, false otherwise
     */
    public boolean isExpired() {
        if (expirationDate == null) {
            return false;
        }
        return expirationDate.before(new Date());
    }
    
    /**
     * Decrease the quantity by the specified amount
     * @param amount The amount to decrease
     * @throws IllegalArgumentException if the amount is greater than the quantity
     */
    public void decreaseQuantity(int amount) {
        if (amount > quantity) {
            throw new IllegalArgumentException("Cannot decrease by more than available quantity");
        }
        quantity -= amount;
    }
    
    /**
     * Increase the quantity by the specified amount
     * @param amount The amount to increase
     */
    public void increaseQuantity(int amount) {
        quantity += amount;
    }

    @Override
    public String toString() {
        return "VaccineStock{" +
                "vaccineId=" + vaccineId +
                ", vaccineName='" + vaccineName + '\'' +
                ", quantity=" + quantity +
                ", reorderLevel=" + reorderLevel +
                ", lowStock=" + isLowStock() +
                '}';
    }
}
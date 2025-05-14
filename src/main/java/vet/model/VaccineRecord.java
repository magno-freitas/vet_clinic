package model;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Represents a vaccine record for a pet
 */
public class VaccineRecord {
    private int id;
    private int petId;
    private String vaccineName;
    private Date applicationDate;
    private Date nextDoseDate;
    private String notes;
    private Date createdAt;

    /**
     * Default constructor
     */
    public VaccineRecord() {
        this.createdAt = new Date();
    }

    /**
     * Constructor with essential fields
     * @param petId The ID of the pet
     * @param vaccineName The name of the vaccine
     * @param applicationDate The date the vaccine was applied
     * @param nextDoseDate The date for the next dose
     */
    public VaccineRecord(int petId, String vaccineName, Date applicationDate, Date nextDoseDate) {
        this.petId = petId;
        this.vaccineName = vaccineName;
        this.applicationDate = applicationDate;
        this.nextDoseDate = nextDoseDate;
        this.createdAt = new Date();
    }

    /**
     * Get the record ID
     * @return The record ID
     */
    public int getId() {
        return id;
    }

    /**
     * Set the record ID
     * @param id The record ID
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the pet ID
     * @return The pet ID
     */
    public int getPetId() {
        return petId;
    }

    /**
     * Set the pet ID
     * @param petId The pet ID
     */
    public void setPetId(int petId) {
        this.petId = petId;
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
     * Get the application date
     * @return The application date
     */
    public Date getApplicationDate() {
        return applicationDate;
    }

    /**
     * Set the application date
     * @param applicationDate The application date
     */
    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    /**
     * Get the next dose date
     * @return The next dose date
     */
    public Date getNextDoseDate() {
        return nextDoseDate;
    }

    /**
     * Set the next dose date
     * @param nextDoseDate The next dose date
     */
    public void setNextDoseDate(Date nextDoseDate) {
        this.nextDoseDate = nextDoseDate;
    }

    /**
     * Get the notes
     * @return The notes
     */
    public String getNotes() {
        return notes;
    }

    /**
     * Set the notes
     * @param notes The notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Get the creation date
     * @return The creation date
     */
    public Date getCreatedAt() {
        return createdAt;
    }

    /**
     * Set the creation date
     * @param createdAt The creation date
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Format the application date as a string
     * @return The formatted application date
     */
    public String getFormattedApplicationDate() {
        if (applicationDate == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(applicationDate);
    }
    
    /**
     * Format the next dose date as a string
     * @return The formatted next dose date
     */
    public String getFormattedNextDoseDate() {
        if (nextDoseDate == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(nextDoseDate);
    }
    
    /**
     * Check if the next dose is due
     * @return true if the next dose is due, false otherwise
     */
    public boolean isNextDoseDue() {
        if (nextDoseDate == null) {
            return false;
        }
        return nextDoseDate.before(new Date());
    }

    @Override
    public String toString() {
        return "VaccineRecord{" +
                "id=" + id +
                ", petId=" + petId +
                ", vaccineName='" + vaccineName + '\'' +
                ", applicationDate=" + getFormattedApplicationDate() +
                ", nextDoseDate=" + getFormattedNextDoseDate() +
                ", notes='" + notes + '\'' +
                '}';
    }
}
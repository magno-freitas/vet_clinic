package vet.model;

import java.util.Date;

import com.vetclinic.vet.model.Pet;

import java.text.SimpleDateFormat;

/**
 * Represents a medical record for a pet
 */
public class MedicalRecord {
    private int id;
    private int petId;
    private Date recordDate;
    private double weight;
    private String temperature;
    private String symptoms;
    private String diagnosis;
    private String treatment;
    private String prescriptions;
    private String notes;
    private Date createdAt;

    /**
     * Default constructor
     */
    public MedicalRecord() {
        this.createdAt = new Date();
    }

    /**
     * Constructor with essential fields
     * @param petId The ID of the pet
     * @param recordDate The date of the record
     * @param diagnosis The diagnosis
     * @param treatment The treatment
     */
    public MedicalRecord(int petId, Date recordDate, String diagnosis, String treatment) {
        this.petId = petId;
        this.recordDate = recordDate;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
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
     * Get the record date
     * @return The record date
     */
    public Date getRecordDate() {
        return recordDate;
    }

    /**
     * Set the record date
     * @param recordDate The record date
     */
    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    /**
     * Get the weight
     * @return The weight
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Set the weight
     * @param weight The weight
     */
    public void setWeight(double weight) {
        this.weight = weight;
    }

    /**
     * Get the temperature
     * @return The temperature
     */
    public String getTemperature() {
        return temperature;
    }

    /**
     * Set the temperature
     * @param temperature The temperature
     */
    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    /**
     * Get the symptoms
     * @return The symptoms
     */
    public String getSymptoms() {
        return symptoms;
    }

    /**
     * Set the symptoms
     * @param symptoms The symptoms
     */
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    /**
     * Get the diagnosis
     * @return The diagnosis
     */
    public String getDiagnosis() {
        return diagnosis;
    }

    /**
     * Set the diagnosis
     * @param diagnosis The diagnosis
     */
    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    /**
     * Get the treatment
     * @return The treatment
     */
    public String getTreatment() {
        return treatment;
    }

    /**
     * Set the treatment
     * @param treatment The treatment
     */
    public void setTreatment(String treatment) {
        this.treatment = treatment;
    }

    /**
     * Get the prescriptions
     * @return The prescriptions
     */
    public String getPrescriptions() {
        return prescriptions;
    }

    /**
     * Set the prescriptions
     * @param prescriptions The prescriptions
     */
    public void setPrescriptions(String prescriptions) {
        this.prescriptions = prescriptions;
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
     * Format the record date as a string
     * @return The formatted record date
     */
    public String getFormattedRecordDate() {
        if (recordDate == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(recordDate);
    }

    @Override
    public String toString() {
        return "MedicalRecord{" +
                "id=" + id +
                ", petId=" + petId +
                ", recordDate=" + getFormattedRecordDate() +
                ", weight=" + weight +
                ", temperature='" + temperature + '\'' +
                ", diagnosis='" + diagnosis + '\'' +
                ", treatment='" + treatment + '\'' +
                '}';
    }

    public void setPet(Pet pet) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setPet'");
    }
}
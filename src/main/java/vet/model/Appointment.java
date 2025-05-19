package vet.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

/**
 * Represents an appointment in the veterinary clinic
 */
public class Appointment {
    private int appointmentId;
    private int petId;
    private Timestamp startTime;
    private Timestamp endTime;
    private String status;
    private String notes;
    private ServiceType serviceType;
    private double price;
    
    /**
     * Default constructor
     */
    public Appointment() {
    }
    
    /**
     * Constructor with essential fields
     * @param petId The ID of the pet
     * @param serviceType The type of service
     * @param startTime The start time of the appointment
     * @param endTime The end time of the appointment
     */
    public Appointment(int petId, ServiceType serviceType, Timestamp startTime, Timestamp endTime) {
        this.petId = petId;
        this.serviceType = serviceType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = "SCHEDULED";
        this.price = serviceType.getDefaultPrice();
    }

    /**
     * Get the appointment ID
     * @return The appointment ID
     */
    public int getAppointmentId() {
        return appointmentId;
    }

    /**
     * Set the appointment ID
     * @param appointmentId The appointment ID
     */
    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
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
     * Get the service description
     * @return The service description
     */
    public String getService() {
        return serviceType != null ? serviceType.getDescription() : null;
    }

    /**
     * Set the service by description
     * @param serviceDescription The service description
     */
    public void setService(String serviceDescription) {
        if (serviceDescription != null) {
            this.serviceType = ServiceType.fromDescription(serviceDescription);
            if (this.serviceType != null && this.price == 0) {
                this.price = this.serviceType.getDefaultPrice();
            }
        }
    }

    /**
     * Get the start time
     * @return The start time
     */
    public Timestamp getStartTime() {
        return startTime;
    }

    /**
     * Set the start time
     * @param startTime The start time
     */
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    /**
     * Get the end time
     * @return The end time
     */
    public Timestamp getEndTime() {
        return endTime;
    }

    /**
     * Set the end time
     * @param endTime The end time
     */
    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    /**
     * Get the status
     * @return The status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Set the status
     * @param status The status
     */
    public void setStatus(String status) {
        this.status = status;
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
     * Get the service type
     * @return The service type
     */
    public ServiceType getServiceType() {
        return serviceType;
    }

    /**
     * Set the service type
     * @param serviceType The service type
     */
    public void setServiceType(ServiceType serviceType) {
        this.serviceType = serviceType;
        if (serviceType != null && this.price == 0) {
            this.price = serviceType.getDefaultPrice();
        }
    }
    
    /**
     * Get the price
     * @return The price
     */
    public double getPrice() {
        return price;
    }
    
    /**
     * Set the price
     * @param price The price
     */
    public void setPrice(double price) {
        this.price = price;
    }
    
    /**
     * Format the start time as a string
     * @return The formatted start time
     */
    public String getFormattedStartTime() {
        if (startTime == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(startTime);
    }
    
    /**
     * Format the end time as a string
     * @return The formatted end time
     */
    public String getFormattedEndTime() {
        if (endTime == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(endTime);
    }
    
    /**
     * Calculate the duration of the appointment in minutes
     * @return The duration in minutes
     */
    public int getDurationMinutes() {
        if (startTime == null || endTime == null) {
            return serviceType != null ? serviceType.getDurationMinutes() : 0;
        }
        return (int) ((endTime.getTime() - startTime.getTime()) / (1000 * 60));
    }
    
    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", petId=" + petId +
                ", service='" + getService() + '\'' +
                ", startTime=" + getFormattedStartTime() +
                ", endTime=" + getFormattedEndTime() +
                ", status='" + status + '\'' +
                ", serviceType=" + serviceType +
                ", price=" + price +
                '}';
    }
}
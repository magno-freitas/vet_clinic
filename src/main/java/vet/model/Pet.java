package vet.model;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Represents a pet in the veterinary clinic
 */
public class Pet {
    private int petId;
    private int clientId;
    private String name;
    private String species;
    private String breed;
    private Date birthDate;
    private boolean vaccinesUpToDate;

    /**
     * Default constructor
     */
    public Pet() {
    }

    /**
     * Constructor with essential fields
     * @param clientId The ID of the pet's owner
     * @param name The pet's name
     * @param species The pet's species
     * @param breed The pet's breed
     * @param birthDate The pet's birth date
     */
    public Pet(int clientId, String name, String species, String breed, Date birthDate) {
        this.clientId = clientId;
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.birthDate = birthDate;
        this.vaccinesUpToDate = false;
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
     * Get the client ID
     * @return The client ID
     */
    public int getClientId() {
        return clientId;
    }

    /**
     * Set the client ID
     * @param clientId The client ID
     */
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    /**
     * Get the pet's name
     * @return The pet's name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the pet's name
     * @param name The pet's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the pet's species
     * @return The pet's species
     */
    public String getSpecies() {
        return species;
    }

    /**
     * Set the pet's species
     * @param species The pet's species
     */
    public void setSpecies(String species) {
        this.species = species;
    }

    /**
     * Get the pet's breed
     * @return The pet's breed
     */
    public String getBreed() {
        return breed;
    }

    /**
     * Set the pet's breed
     * @param breed The pet's breed
     */
    public void setBreed(String breed) {
        this.breed = breed;
    }

    /**
     * Get the pet's birth date
     * @return The pet's birth date as a java.sql.Date
     */
    public java.sql.Date getBirthDate() {
        // Convert java.util.Date to java.sql.Date
        return birthDate != null ? new java.sql.Date(birthDate.getTime()) : null;
    }
    
    /**
     * Get the pet's birth date as a java.util.Date
     * @return The pet's birth date
     */
    public Date getBirthDateAsUtilDate() {
        return birthDate;
    }

    /**
     * Set the pet's birth date
     * @param birthDate The pet's birth date
     */
    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }
    
    /**
     * Check if the pet's vaccines are up to date
     * @return true if the pet's vaccines are up to date, false otherwise
     */
    public boolean isVaccinesUpToDate() {
        return vaccinesUpToDate;
    }
    
    /**
     * Set whether the pet's vaccines are up to date
     * @param vaccinesUpToDate true if the pet's vaccines are up to date, false otherwise
     */
    public void setVaccinesUpToDate(boolean vaccinesUpToDate) {
        this.vaccinesUpToDate = vaccinesUpToDate;
    }
    
    /**
     * Calculate the pet's age in years
     * @return The pet's age in years
     */
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        
        Date now = new Date();
        long diffInMillis = now.getTime() - birthDate.getTime();
        return (int) (diffInMillis / (1000L * 60 * 60 * 24 * 365));
    }
    
    /**
     * Format the birth date as a string
     * @return The formatted birth date
     */
    public String getFormattedBirthDate() {
        if (birthDate == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(birthDate);
    }

    @Override
    public String toString() {
        return "Pet{" +
                "petId=" + petId +
                ", clientId=" + clientId +
                ", name='" + name + '\'' +
                ", species='" + species + '\'' +
                ", breed='" + breed + '\'' +
                ", birthDate=" + getFormattedBirthDate() +
                ", age=" + getAge() +
                ", vaccinesUpToDate=" + vaccinesUpToDate +
                '}';
    }
}
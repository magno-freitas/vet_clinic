

import java.sql.Date;

public class Pet {
    private int petId;
    private int clientId;
    private String name;
    private String species;
    private String breed;
    private java.util.Date birthDate;

    public int getPetId() {
        return petId;
    }

    public void setPetId(int petId) {
        this.petId = petId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public java.sql.Date getBirthDate() {
        // Converter java.util.Date para java.sql.Date
        return birthDate != null ? new java.sql.Date(birthDate.getTime()) : null;
    }

    public void setBirthDate(java.util.Date birthDate) {
        this.birthDate = birthDate;
    }
}
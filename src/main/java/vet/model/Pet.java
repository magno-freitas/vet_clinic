package vet.model;

import java.sql.Date;

public class Pet {
    private int petId;
    private int clientId;
    private String name;
    private String species;
    private String breed;
    private Date birthDate;
    private int age;

    // Basic getters and setters
    public int getPetId() { return petId; }
    public void setPetId(int petId) { this.petId = petId; }
    
    public int getClientId() { return clientId; }
    public void setClientId(int clientId) { this.clientId = clientId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getSpecies() { return species; }
    public void setSpecies(String species) { this.species = species; }
    
    public String getBreed() { return breed; }
    public void setBreed(String breed) { this.breed = breed; }
    
    public Date getBirthDate() { return birthDate; }
    public void setBirthDate(Date birthDate) { this.birthDate = birthDate; }
    
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
}
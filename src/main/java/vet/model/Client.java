package vet.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a client in the veterinary clinic
 */
public class Client {
    private int clientId;
    private String name;
    private String email;
    private String phone;
    private String address;
    private List<Pet> pets;

    /**
     * Default constructor
     */
    public Client() {
        this.pets = new ArrayList<>();
    }

    /**
     * Constructor with essential fields
     * @param name The client's name
     * @param email The client's email
     * @param phone The client's phone number
     * @param address The client's address
     */
    public Client(String name, String email, String phone, String address) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.pets = new ArrayList<>();
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
     * Get the client's name
     * @return The client's name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the client's name
     * @param name The client's name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the client's email
     * @return The client's email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Set the client's email
     * @param email The client's email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the client's phone number
     * @return The client's phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Set the client's phone number
     * @param phone The client's phone number
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Get the client's address
     * @return The client's address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Set the client's address
     * @param address The client's address
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Get the client's pets
     * @return The client's pets
     */
    public List<Pet> getPets() {
        return pets;
    }
    
    /**
     * Set the client's pets
     * @param pets The client's pets
     */
    public void setPets(List<Pet> pets) {
        this.pets = pets;
    }
    
    /**
     * Add a pet to the client
     * @param pet The pet to add
     */
    public void addPet(Pet pet) {
        if (this.pets == null) {
            this.pets = new ArrayList<>();
        }
        pet.setClientId(this.clientId);
        this.pets.add(pet);
    }
    
    /**
     * Remove a pet from the client
     * @param pet The pet to remove
     * @return true if the pet was removed, false otherwise
     */
    public boolean removePet(Pet pet) {
        if (this.pets == null) {
            return false;
        }
        return this.pets.remove(pet);
    }
    
    /**
     * Get the number of pets
     * @return The number of pets
     */
    public int getPetCount() {
        if (this.pets == null) {
            return 0;
        }
        return this.pets.size();
    }

    @Override
    public String toString() {
        return "Client{" +
                "clientId=" + clientId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", petCount=" + getPetCount() +
                '}';
    }
}
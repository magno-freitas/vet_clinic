package vet.service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import vet.model.Pet;
import vet.util.ConnectionPool;
import vet.util.LoggerUtil;
import vet.util.ValidationUtil;
import vet.exception.DatabaseException;
import vet.exception.ValidationException;

/**
 * Service class for managing pets
 */
public class PetService {
    private static final Logger logger = LoggerUtil.getLogger(PetService.class);
    
    /**
     * Create a new pet
     * @param pet The pet to create
     * @throws DatabaseException If there is a database error
     * @throws ValidationException If the pet data is invalid
     */
    public void createPet(Pet pet) throws DatabaseException, ValidationException {
        validatePet(pet);
        
        String query = "INSERT INTO pets (client_id, name, species, breed, birth_date) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, pet.getClientId());
            stmt.setString(2, pet.getName());
            stmt.setString(3, pet.getSpecies());
            stmt.setString(4, pet.getBreed());
            
            if (pet.getBirthDate() != null) {
                stmt.setDate(5, new Date(pet.getBirthDate().getTime()));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    pet.setPetId(rs.getInt(1));
                    logger.info("Created pet with ID: " + pet.getPetId());
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating pet", e);
            throw new DatabaseException("Error creating pet: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update an existing pet
     * @param pet The pet to update
     * @throws DatabaseException If there is a database error
     * @throws ValidationException If the pet data is invalid
     */
    public void updatePet(Pet pet) throws DatabaseException, ValidationException {
        validatePet(pet);
        
        String query = "UPDATE pets SET client_id = ?, name = ?, species = ?, breed = ?, birth_date = ? WHERE pet_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, pet.getClientId());
            stmt.setString(2, pet.getName());
            stmt.setString(3, pet.getSpecies());
            stmt.setString(4, pet.getBreed());
            
            if (pet.getBirthDate() != null) {
                stmt.setDate(5, new Date(pet.getBirthDate().getTime()));
            } else {
                stmt.setNull(5, java.sql.Types.DATE);
            }
            
            stmt.setInt(6, pet.getPetId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Updated pet with ID: " + pet.getPetId());
            } else {
                logger.warning("No pet found with ID: " + pet.getPetId());
                throw new ValidationException("Pet not found with ID: " + pet.getPetId());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating pet", e);
            throw new DatabaseException("Error updating pet: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete a pet
     * @param petId The pet ID
     * @throws DatabaseException If there is a database error
     */
    public void deletePet(int petId) throws DatabaseException {
        String query = "DELETE FROM pets WHERE pet_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, petId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Deleted pet with ID: " + petId);
            } else {
                logger.warning("No pet found with ID: " + petId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting pet", e);
            throw new DatabaseException("Error deleting pet: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get a pet by ID
     * @param petId The pet ID
     * @return The pet
     * @throws DatabaseException If there is a database error
     */
    public Pet getPetById(int petId) throws DatabaseException {
        String query = "SELECT * FROM pets WHERE pet_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, petId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Pet pet = mapResultSetToPet(rs);
                    logger.info("Retrieved pet with ID: " + petId);
                    return pet;
                } else {
                    logger.warning("No pet found with ID: " + petId);
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving pet", e);
            throw new DatabaseException("Error retrieving pet: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get all pets for a client
     * @param clientId The client ID
     * @return List of pets for the client
     * @throws DatabaseException If there is a database error
     */
    public List<Pet> getPetsByClientId(int clientId) throws DatabaseException {
        String query = "SELECT * FROM pets WHERE client_id = ? ORDER BY name";
        List<Pet> pets = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, clientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pets.add(mapResultSetToPet(rs));
                }
            }
            
            logger.info("Retrieved " + pets.size() + " pets for client ID: " + clientId);
            return pets;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving pets for client", e);
            throw new DatabaseException("Error retrieving pets for client: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get all pets
     * @return List of all pets
     * @throws DatabaseException If there is a database error
     */
    public List<Pet> getAllPets() throws DatabaseException {
        String query = "SELECT * FROM pets ORDER BY name";
        List<Pet> pets = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                pets.add(mapResultSetToPet(rs));
            }
            
            logger.info("Retrieved " + pets.size() + " pets");
            return pets;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving pets", e);
            throw new DatabaseException("Error retrieving pets: " + e.getMessage(), e);
        }
    }
    
    /**
     * Search for pets by name or species
     * @param searchTerm The search term
     * @return List of matching pets
     * @throws DatabaseException If there is a database error
     */
    public List<Pet> searchPets(String searchTerm) throws DatabaseException {
        String query = "SELECT * FROM pets WHERE name LIKE ? OR species LIKE ? OR breed LIKE ? ORDER BY name";
        List<Pet> pets = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    pets.add(mapResultSetToPet(rs));
                }
            }
            
            logger.info("Found " + pets.size() + " pets matching search term: " + searchTerm);
            return pets;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching for pets", e);
            throw new DatabaseException("Error searching for pets: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validate pet data
     * @param pet The pet to validate
     * @throws ValidationException If the pet data is invalid
     */
    private void validatePet(Pet pet) throws ValidationException {
        if (pet.getClientId() <= 0) {
            throw new ValidationException("Client ID is required");
        }
        
        ValidationUtil.validateRequired(pet.getName(), "Name");
        ValidationUtil.validateMaxLength(pet.getName(), 255, "Name");
        
        ValidationUtil.validateRequired(pet.getSpecies(), "Species");
        ValidationUtil.validateMaxLength(pet.getSpecies(), 50, "Species");
        
        if (pet.getBreed() != null) {
            ValidationUtil.validateMaxLength(pet.getBreed(), 50, "Breed");
        }
    }
    
    /**
     * Map a ResultSet to a Pet object
     * @param rs The ResultSet
     * @return The Pet object
     * @throws SQLException If there is a database error
     */
    private Pet mapResultSetToPet(ResultSet rs) throws SQLException {
        Pet pet = new Pet();
        pet.setPetId(rs.getInt("pet_id"));
        pet.setClientId(rs.getInt("client_id"));
        pet.setName(rs.getString("name"));
        pet.setSpecies(rs.getString("species"));
        pet.setBreed(rs.getString("breed"));
        pet.setBirthDate(rs.getDate("birth_date"));
        return pet;
    }
}
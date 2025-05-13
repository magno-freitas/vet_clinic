

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Date;

public class PetService {
    
    public int addPet(Pet pet) throws SQLException {
        String query = "INSERT INTO pets (client_id, name, species, breed, birth_date) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, pet.getClientId());
            stmt.setString(2, pet.getName());
            stmt.setString(3, pet.getSpecies());
            stmt.setString(4, pet.getBreed());
            stmt.setDate(5, pet.getBirthDate());

            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    pet.setPetId(generatedKeys.getInt(1));
                    return pet.getPetId();
                }
            }
        }
        return -1;
    }

    public Pet getPetById(int petId) throws SQLException {
        String query = "SELECT * FROM pets WHERE pet_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, petId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
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
        return null;
    }

    public int getPetIdByClientAndName(int clientId, String petName) throws SQLException {
        String query = "SELECT pet_id FROM pets WHERE client_id = ? AND name = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, clientId);
            stmt.setString(2, petName);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("pet_id");
            }
        }
        return -1;
    }

    public boolean checkVaccinesUpToDate(int petId) throws SQLException {
        String query = "SELECT COUNT(*) FROM pet_vaccines WHERE pet_id = ? AND expiry_date > CURRENT_DATE";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, petId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }

    public int getClientPetCount(int clientId) throws SQLException {
        String query = "SELECT COUNT(*) FROM pets WHERE client_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        }
    }

    public List<Pet> getAllPets() throws SQLException {
        String query = "SELECT * FROM pets";
        List<Pet> pets = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Pet pet = new Pet();
                pet.setPetId(rs.getInt("pet_id"));
                pet.setClientId(rs.getInt("client_id"));
                pet.setName(rs.getString("name"));
                pet.setSpecies(rs.getString("species"));
                pet.setBreed(rs.getString("breed"));
                pet.setBirthDate(rs.getDate("birth_date"));
                pets.add(pet);
            }
        }
        return pets;
    }

    public void deletePet(int petId) throws SQLException {
        String query = "DELETE FROM pets WHERE pet_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, petId);
            stmt.executeUpdate();
        }
    }
    public void updatePet(Pet pet) throws SQLException {
        String query = "UPDATE pets SET client_id = ?, name = ?, species = ?, breed = ?, birth_date = ? WHERE pet_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, pet.getClientId());
            stmt.setString(2, pet.getName());
            stmt.setString(3, pet.getSpecies());
            stmt.setString(4, pet.getBreed());
            stmt.setDate(5, pet.getBirthDate());
            stmt.setInt(6, pet.getPetId());

            stmt.executeUpdate();
        }
    }
    public List<Pet> getPetsByClientId(int clientId) throws SQLException {
        String query = "SELECT * FROM pets WHERE client_id = ?";
        List<Pet> pets = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Pet pet = new Pet();
                pet.setPetId(rs.getInt("pet_id"));
                pet.setClientId(rs.getInt("client_id"));
                pet.setName(rs.getString("name"));
                pet.setSpecies(rs.getString("species"));
                pet.setBreed(rs.getString("breed"));
                pet.setBirthDate(rs.getDate("birth_date"));
                pets.add(pet);
            }
        }
        return pets;
    }
    public List<Pet> getPetsBySpecies(String species) throws SQLException {
        String query = "SELECT * FROM pets WHERE species = ?";
        List<Pet> pets = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, species);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Pet pet = new Pet();
                pet.setPetId(rs.getInt("pet_id"));
                pet.setClientId(rs.getInt("client_id"));
                pet.setName(rs.getString("name"));
                pet.setSpecies(rs.getString("species"));
                pet.setBreed(rs.getString("breed"));
                pet.setBirthDate(rs.getDate("birth_date"));
                pets.add(pet);
            }
        }
        return pets;
    }
}
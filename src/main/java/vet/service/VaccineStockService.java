package vet.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import vet.model.VaccineStock;
import vet.util.ConnectionPool;
import vet.util.LoggerUtil;
import vet.util.ValidationUtil;
import vet.exception.DatabaseException;
import vet.exception.ValidationException;

/**
 * Service class for managing vaccine stock
 */
public class VaccineStockService {
    private static final Logger logger = LoggerUtil.getLogger(VaccineStockService.class);
    
    /**
     * Add a new vaccine to stock
     * @param vaccine The vaccine to add
     * @throws DatabaseException If there is a database error
     * @throws ValidationException If the vaccine data is invalid
     */
    public void addVaccine(VaccineStock vaccine) throws DatabaseException, ValidationException {
        validateVaccine(vaccine);
        
        String query = "INSERT INTO vaccine_stock (vaccine_name, quantity, reorder_level) VALUES (?, ?, ?)";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, vaccine.getVaccineName());
            stmt.setInt(2, vaccine.getQuantity());
            stmt.setInt(3, vaccine.getReorderLevel());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    vaccine.setVaccineId(rs.getInt(1));
                    logger.info("Added vaccine to stock with ID: " + vaccine.getVaccineId());
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error adding vaccine to stock", e);
            throw new DatabaseException("Error adding vaccine to stock: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update vaccine stock
     * @param vaccine The vaccine to update
     * @throws DatabaseException If there is a database error
     * @throws ValidationException If the vaccine data is invalid
     */
    public void updateVaccine(VaccineStock vaccine) throws DatabaseException, ValidationException {
        validateVaccine(vaccine);
        
        String query = "UPDATE vaccine_stock SET vaccine_name = ?, quantity = ?, reorder_level = ? WHERE vaccine_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, vaccine.getVaccineName());
            stmt.setInt(2, vaccine.getQuantity());
            stmt.setInt(3, vaccine.getReorderLevel());
            stmt.setInt(4, vaccine.getVaccineId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Updated vaccine stock with ID: " + vaccine.getVaccineId());
            } else {
                logger.warning("No vaccine found with ID: " + vaccine.getVaccineId());
                throw new ValidationException("Vaccine not found with ID: " + vaccine.getVaccineId());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating vaccine stock", e);
            throw new DatabaseException("Error updating vaccine stock: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete a vaccine from stock
     * @param vaccineId The vaccine ID
     * @throws DatabaseException If there is a database error
     */
    public void deleteVaccine(int vaccineId) throws DatabaseException {
        String query = "DELETE FROM vaccine_stock WHERE vaccine_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, vaccineId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Deleted vaccine with ID: " + vaccineId);
            } else {
                logger.warning("No vaccine found with ID: " + vaccineId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting vaccine", e);
            throw new DatabaseException("Error deleting vaccine: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get a vaccine by ID
     * @param vaccineId The vaccine ID
     * @return The vaccine
     * @throws DatabaseException If there is a database error
     */
    public VaccineStock getVaccineById(int vaccineId) throws DatabaseException {
        String query = "SELECT * FROM vaccine_stock WHERE vaccine_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, vaccineId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    VaccineStock vaccine = mapResultSetToVaccine(rs);
                    logger.info("Retrieved vaccine with ID: " + vaccineId);
                    return vaccine;
                } else {
                    logger.warning("No vaccine found with ID: " + vaccineId);
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving vaccine", e);
            throw new DatabaseException("Error retrieving vaccine: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get all vaccines in stock
     * @return List of all vaccines
     * @throws DatabaseException If there is a database error
     */
    public List<VaccineStock> getAllVaccines() throws DatabaseException {
        String query = "SELECT * FROM vaccine_stock ORDER BY vaccine_name";
        List<VaccineStock> vaccines = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                vaccines.add(mapResultSetToVaccine(rs));
            }
            
            logger.info("Retrieved " + vaccines.size() + " vaccines");
            return vaccines;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving vaccines", e);
            throw new DatabaseException("Error retrieving vaccines: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get vaccines that need to be reordered
     * @return List of vaccines that need to be reordered
     * @throws DatabaseException If there is a database error
     */
    public List<VaccineStock> getVaccinesNeedingReorder() throws DatabaseException {
        String query = "SELECT * FROM vaccine_stock WHERE quantity <= reorder_level ORDER BY quantity";
        List<VaccineStock> vaccines = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                vaccines.add(mapResultSetToVaccine(rs));
            }
            
            logger.info("Found " + vaccines.size() + " vaccines needing reorder");
            return vaccines;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving vaccines needing reorder", e);
            throw new DatabaseException("Error retrieving vaccines needing reorder: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update vaccine quantity
     * @param vaccineId The vaccine ID
     * @param quantityChange The change in quantity (positive for increase, negative for decrease)
     * @throws DatabaseException If there is a database error
     * @throws ValidationException If the resulting quantity would be negative
     */
    public void updateVaccineQuantity(int vaccineId, int quantityChange) throws DatabaseException, ValidationException {
        String query = "UPDATE vaccine_stock SET quantity = quantity + ? WHERE vaccine_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Check if the resulting quantity would be negative
            if (quantityChange < 0) {
                VaccineStock vaccine = getVaccineById(vaccineId);
                if (vaccine != null && vaccine.getQuantity() + quantityChange < 0) {
                    throw new ValidationException("Cannot reduce quantity below zero");
                }
            }
            
            stmt.setInt(1, quantityChange);
            stmt.setInt(2, vaccineId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Updated quantity for vaccine ID " + vaccineId + " by " + quantityChange);
            } else {
                logger.warning("No vaccine found with ID: " + vaccineId);
                throw new ValidationException("Vaccine not found with ID: " + vaccineId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating vaccine quantity", e);
            throw new DatabaseException("Error updating vaccine quantity: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validate vaccine data
     * @param vaccine The vaccine to validate
     * @throws ValidationException If the vaccine data is invalid
     */
    private void validateVaccine(VaccineStock vaccine) throws ValidationException {
        ValidationUtil.validateRequired(vaccine.getVaccineName(), "Vaccine name");
        ValidationUtil.validateMaxLength(vaccine.getVaccineName(), 255, "Vaccine name");
        
        ValidationUtil.validateNonNegative(vaccine.getQuantity(), "Quantity");
        ValidationUtil.validateNonNegative(vaccine.getReorderLevel(), "Reorder level");
    }
    
    /**
     * Map a ResultSet to a VaccineStock object
     * @param rs The ResultSet
     * @return The VaccineStock object
     * @throws SQLException If there is a database error
     */
    private VaccineStock mapResultSetToVaccine(ResultSet rs) throws SQLException {
        VaccineStock vaccine = new VaccineStock();
        vaccine.setVaccineId(rs.getInt("vaccine_id"));
        vaccine.setVaccineName(rs.getString("vaccine_name"));
        vaccine.setQuantity(rs.getInt("quantity"));
        vaccine.setReorderLevel(rs.getInt("reorder_level"));
        return vaccine;
    }
}
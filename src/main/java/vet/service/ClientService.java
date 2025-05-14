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

import vet.model.Client;
import vet.util.ConnectionPool;
import vet.util.LoggerUtil;
import vet.util.ValidationUtil;
import vet.exception.DatabaseException;
import vet.exception.ValidationException;

/**
 * Service class for managing clients
 */
public class ClientService {
    private static final Logger logger = LoggerUtil.getLogger(ClientService.class);
    
    /**
     * Create a new client
     * @param client The client to create
     * @throws DatabaseException If there is a database error
     * @throws ValidationException If the client data is invalid
     */
    public void createClient(Client client) throws DatabaseException, ValidationException {
        validateClient(client);
        
        String query = "INSERT INTO clients (name, email, phone, address) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getPhone());
            stmt.setString(4, client.getAddress());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    client.setClientId(rs.getInt(1));
                    logger.info("Created client with ID: " + client.getClientId());
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating client", e);
            throw new DatabaseException("Error creating client: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update an existing client
     * @param client The client to update
     * @throws DatabaseException If there is a database error
     * @throws ValidationException If the client data is invalid
     */
    public void updateClient(Client client) throws DatabaseException, ValidationException {
        validateClient(client);
        
        String query = "UPDATE clients SET name = ?, email = ?, phone = ?, address = ? WHERE client_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getPhone());
            stmt.setString(4, client.getAddress());
            stmt.setInt(5, client.getClientId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Updated client with ID: " + client.getClientId());
            } else {
                logger.warning("No client found with ID: " + client.getClientId());
                throw new ValidationException("Client not found with ID: " + client.getClientId());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating client", e);
            throw new DatabaseException("Error updating client: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete a client
     * @param clientId The client ID
     * @throws DatabaseException If there is a database error
     */
    public void deleteClient(int clientId) throws DatabaseException {
        String query = "DELETE FROM clients WHERE client_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, clientId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Deleted client with ID: " + clientId);
            } else {
                logger.warning("No client found with ID: " + clientId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting client", e);
            throw new DatabaseException("Error deleting client: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get a client by ID
     * @param clientId The client ID
     * @return The client
     * @throws DatabaseException If there is a database error
     */
    public Client getClientById(int clientId) throws DatabaseException {
        String query = "SELECT * FROM clients WHERE client_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, clientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Client client = mapResultSetToClient(rs);
                    logger.info("Retrieved client with ID: " + clientId);
                    return client;
                } else {
                    logger.warning("No client found with ID: " + clientId);
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving client", e);
            throw new DatabaseException("Error retrieving client: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get all clients
     * @return List of all clients
     * @throws DatabaseException If there is a database error
     */
    public List<Client> getAllClients() throws DatabaseException {
        String query = "SELECT * FROM clients ORDER BY name";
        List<Client> clients = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                clients.add(mapResultSetToClient(rs));
            }
            
            logger.info("Retrieved " + clients.size() + " clients");
            return clients;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving clients", e);
            throw new DatabaseException("Error retrieving clients: " + e.getMessage(), e);
        }
    }
    
    /**
     * Search for clients by name or email
     * @param searchTerm The search term
     * @return List of matching clients
     * @throws DatabaseException If there is a database error
     */
    public List<Client> searchClients(String searchTerm) throws DatabaseException {
        String query = "SELECT * FROM clients WHERE name LIKE ? OR email LIKE ? ORDER BY name";
        List<Client> clients = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            String searchPattern = "%" + searchTerm + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    clients.add(mapResultSetToClient(rs));
                }
            }
            
            logger.info("Found " + clients.size() + " clients matching search term: " + searchTerm);
            return clients;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching for clients", e);
            throw new DatabaseException("Error searching for clients: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validate client data
     * @param client The client to validate
     * @throws ValidationException If the client data is invalid
     */
    private void validateClient(Client client) throws ValidationException {
        ValidationUtil.validateRequired(client.getName(), "Name");
        ValidationUtil.validateMaxLength(client.getName(), 255, "Name");
        
        ValidationUtil.validateEmail(client.getEmail());
        ValidationUtil.validateMaxLength(client.getEmail(), 255, "Email");
        
        ValidationUtil.validatePhone(client.getPhone());
        ValidationUtil.validateMaxLength(client.getPhone(), 20, "Phone");
        
        if (client.getAddress() != null) {
            ValidationUtil.validateMaxLength(client.getAddress(), 255, "Address");
        }
    }
    
    /**
     * Map a ResultSet to a Client object
     * @param rs The ResultSet
     * @return The Client object
     * @throws SQLException If there is a database error
     */
    private Client mapResultSetToClient(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setClientId(rs.getInt("client_id"));
        client.setName(rs.getString("name"));
        client.setEmail(rs.getString("email"));
        client.setPhone(rs.getString("phone"));
        client.setAddress(rs.getString("address"));
        return client;
    }
}
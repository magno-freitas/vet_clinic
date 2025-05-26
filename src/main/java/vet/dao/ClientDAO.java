package vet.dao;

import vet.model.Client;
import vet.exception.DatabaseException;
import java.util.List;

/**
 * Data Access Object interface for Client entities
 */
public interface ClientDAO extends DAO<Client, Integer> {
    
    /**
     * Find a client by email
     * @param email The email to search for
     * @return The client or null if not found
     * @throws DatabaseException if a database error occurs
     */
    Client findByEmail(String email) throws DatabaseException;
    
    /**
     * Find a client by phone number
     * @param phone The phone number to search for
     * @return The client or null if not found
     * @throws DatabaseException if a database error occurs
     */
    Client findByPhone(String phone) throws DatabaseException;
    
    /**
     * Find clients by name (partial match)
     * @param name The name to search for
     * @return A list of matching clients
     * @throws DatabaseException if a database error occurs
     */
    List<Client> findByName(String name) throws DatabaseException;
    
    /**
     * Get the ID of a client by email and phone
     * @param email The email to search for
     * @param phone The phone number to search for
     * @return The client ID or -1 if not found
     * @throws DatabaseException if a database error occurs
     */
    int getClientId(String email, String phone) throws DatabaseException;
    
    /**
     * Get all clients with their pets
     * @return A list of clients with their pets loaded
     * @throws DatabaseException if a database error occurs
     */
    List<Client> findAllWithPets() throws DatabaseException;
    
    /**
     * Get a client with their pets
     * @param clientId The client ID
     * @return The client with their pets loaded or null if not found
     * @throws DatabaseException if a database error occurs
     */
    Client findByIdWithPets(int clientId) throws DatabaseException;
}
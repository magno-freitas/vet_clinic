package vet.dao;

import java.util.List;
import vet.exception.DatabaseException;

/**
 * Generic Data Access Object interface for CRUD operations
 * @param <T> The entity type
 * @param <K> The primary key type
 */
public interface DAO<T, K> {
    
    /**
     * Create a new entity
     * @param entity The entity to create
     * @return The created entity with its ID set
     * @throws DatabaseException if a database error occurs
     */
    T create(T entity) throws DatabaseException;
    
    /**
     * Get an entity by its ID
     * @param id The entity ID
     * @return The entity or null if not found
     * @throws DatabaseException if a database error occurs
     */
    T findById(K id) throws DatabaseException;
    
    /**
     * Get all entities
     * @return A list of all entities
     * @throws DatabaseException if a database error occurs
     */
    List<T> findAll() throws DatabaseException;
    
    /**
     * Update an entity
     * @param entity The entity to update
     * @return The updated entity
     * @throws DatabaseException if a database error occurs
     */
    T update(T entity) throws DatabaseException;
    
    /**
     * Delete an entity by its ID
     * @param id The entity ID
     * @return true if the entity was deleted, false otherwise
     * @throws DatabaseException if a database error occurs
     */
    boolean delete(K id) throws DatabaseException;
}
package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.User;
import model.UserRole;
import util.ConnectionPool;
import util.LoggerUtil;
import util.ValidationUtil;
import util.PasswordUtils;
import exception.DatabaseException;
import exception.ValidationException;

/**
 * Service class for managing users
 */
public class UserService {
    private static final Logger logger = LoggerUtil.getLogger(UserService.class);
    
    /**
     * Create a new user
     * @param user The user to create
     * @throws DatabaseException If there is a database error
     * @throws ValidationException If the user data is invalid
     */
    public void createUser(User user) throws DatabaseException, ValidationException {
        validateUser(user);
        
        String query = "INSERT INTO users (username, password, role) VALUES (?, ?, ?)";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, PasswordUtils.hashPassword(user.getPassword()));
            stmt.setString(3, user.getRole().name());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    user.setUserId(rs.getInt(1));
                    logger.info("Created user with ID: " + user.getUserId());
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error creating user", e);
            throw new DatabaseException("Error creating user: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update an existing user
     * @param user The user to update
     * @param updatePassword Whether to update the password
     * @throws DatabaseException If there is a database error
     * @throws ValidationException If the user data is invalid
     */
    public void updateUser(User user, boolean updatePassword) throws DatabaseException, ValidationException {
        validateUser(user);
        
        String query;
        if (updatePassword) {
            query = "UPDATE users SET username = ?, password = ?, role = ? WHERE user_id = ?";
        } else {
            query = "UPDATE users SET username = ?, role = ? WHERE user_id = ?";
        }
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, user.getUsername());
            
            if (updatePassword) {
                stmt.setString(2, PasswordUtils.hashPassword(user.getPassword()));
                stmt.setString(3, user.getRole().name());
                stmt.setInt(4, user.getUserId());
            } else {
                stmt.setString(2, user.getRole().name());
                stmt.setInt(3, user.getUserId());
            }
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Updated user with ID: " + user.getUserId());
            } else {
                logger.warning("No user found with ID: " + user.getUserId());
                throw new ValidationException("User not found with ID: " + user.getUserId());
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating user", e);
            throw new DatabaseException("Error updating user: " + e.getMessage(), e);
        }
    }
    
    /**
     * Delete a user
     * @param userId The user ID
     * @throws DatabaseException If there is a database error
     */
    public void deleteUser(int userId) throws DatabaseException {
        String query = "DELETE FROM users WHERE user_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Deleted user with ID: " + userId);
            } else {
                logger.warning("No user found with ID: " + userId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error deleting user", e);
            throw new DatabaseException("Error deleting user: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get a user by ID
     * @param userId The user ID
     * @return The user
     * @throws DatabaseException If there is a database error
     */
    public User getUserById(int userId) throws DatabaseException {
        String query = "SELECT * FROM users WHERE user_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    logger.info("Retrieved user with ID: " + userId);
                    return user;
                } else {
                    logger.warning("No user found with ID: " + userId);
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving user", e);
            throw new DatabaseException("Error retrieving user: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get a user by username
     * @param username The username
     * @return The user
     * @throws DatabaseException If there is a database error
     */
    public User getUserByUsername(String username) throws DatabaseException {
        String query = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    User user = mapResultSetToUser(rs);
                    logger.info("Retrieved user with username: " + username);
                    return user;
                } else {
                    logger.warning("No user found with username: " + username);
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving user by username", e);
            throw new DatabaseException("Error retrieving user: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get all users
     * @return List of all users
     * @throws DatabaseException If there is a database error
     */
    public List<User> getAllUsers() throws DatabaseException {
        String query = "SELECT * FROM users ORDER BY username";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapResultSetToUser(rs));
            }
            
            logger.info("Retrieved " + users.size() + " users");
            return users;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving users", e);
            throw new DatabaseException("Error retrieving users: " + e.getMessage(), e);
        }
    }
    
    /**
     * Authenticate a user
     * @param username The username
     * @param password The password
     * @return The authenticated user, or null if authentication fails
     * @throws DatabaseException If there is a database error
     */
    public User authenticate(String username, String password) throws DatabaseException {
        String query = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (PasswordUtils.verifyPassword(password, storedHash)) {
                        User user = mapResultSetToUser(rs);
                        logger.info("User authenticated: " + username);
                        return user;
                    } else {
                        logger.warning("Authentication failed for user: " + username);
                        return null;
                    }
                } else {
                    logger.warning("No user found with username: " + username);
                    return null;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error authenticating user", e);
            throw new DatabaseException("Error authenticating user: " + e.getMessage(), e);
        }
    }
    
    /**
     * Change a user's password
     * @param userId The user ID
     * @param oldPassword The old password
     * @param newPassword The new password
     * @return True if the password was changed successfully, false otherwise
     * @throws DatabaseException If there is a database error
     * @throws ValidationException If the new password is invalid
     */
    public boolean changePassword(int userId, String oldPassword, String newPassword) throws DatabaseException, ValidationException {
        // Validate the new password
        ValidationUtil.validateRequired(newPassword, "Password");
        ValidationUtil.validateMinLength(newPassword, 8, "Password");
        
        // Get the user
        User user = getUserById(userId);
        if (user == null) {
            logger.warning("No user found with ID: " + userId);
            return false;
        }
        
        // Verify the old password
        String query = "SELECT password FROM users WHERE user_id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (!PasswordUtils.verifyPassword(oldPassword, storedHash)) {
                        logger.warning("Old password verification failed for user ID: " + userId);
                        return false;
                    }
                } else {
                    logger.warning("No user found with ID: " + userId);
                    return false;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error verifying old password", e);
            throw new DatabaseException("Error verifying old password: " + e.getMessage(), e);
        }
        
        // Update the password
        String updateQuery = "UPDATE users SET password = ? WHERE user_id = ?";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            
            stmt.setString(1, PasswordUtils.hashPassword(newPassword));
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Changed password for user ID: " + userId);
                return true;
            } else {
                logger.warning("Failed to change password for user ID: " + userId);
                return false;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error changing password", e);
            throw new DatabaseException("Error changing password: " + e.getMessage(), e);
        }
    }
    
    /**
     * Validate user data
     * @param user The user to validate
     * @throws ValidationException If the user data is invalid
     */
    private void validateUser(User user) throws ValidationException {
        ValidationUtil.validateRequired(user.getUsername(), "Username");
        ValidationUtil.validateMinLength(user.getUsername(), 3, "Username");
        ValidationUtil.validateMaxLength(user.getUsername(), 50, "Username");
        
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            ValidationUtil.validateMinLength(user.getPassword(), 8, "Password");
        }
        
        if (user.getRole() == null) {
            throw new ValidationException("Role is required");
        }
    }
    
    /**
     * Map a ResultSet to a User object
     * @param rs The ResultSet
     * @return The User object
     * @throws SQLException If there is a database error
     */
    private User mapResultSetToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        // Don't set the password for security reasons
        user.setRole(UserRole.valueOf(rs.getString("role")));
        return user;
    }
}
package vet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientDAO {
    private static final Logger logger = Logger.getLogger(ClientDAO.class.getName());

    public void save(Client client) throws SQLException {
        String sql = "INSERT INTO clients (name, email, phone, address) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, client.getName());
            stmt.setString(2, client.getEmail());
            stmt.setString(3, client.getName());
            stmt.setString(4, client.getAddress());
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    client.setClientId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving client", e);
            throw e;
        }
    }

    public List<Client> findAll() throws SQLException {
        List<Client> clients = new ArrayList<>();
        String sql = "SELECT * FROM clients";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Client client = new Client();
                client.setClientId(rs.getInt("client_id"));
                client.setPhone(rs.getString("name"));
                client.setEmail(rs.getString("email"));
                client.setPhone(rs.getString("phone"));
                client.setAddress(rs.getString("address"));
                clients.add(client);
            }
        }
        return clients;
    }
}

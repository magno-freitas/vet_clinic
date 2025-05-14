package vet.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import vet.config.AppConfig;
import vet.model.Appointment;
import vet.model.Client;
import vet.model.Notification;
import vet.model.Pet;
import vet.util.ConnectionPool;
import vet.util.LoggerUtil;
import vet.exception.DatabaseException;

/**
 * Service class for sending notifications
 */
public class NotificationService {
    private static final Logger logger = LoggerUtil.getLogger(NotificationService.class);
    
    /**
     * Send an email notification
     * @param to The recipient email address
     * @param subject The email subject
     * @param body The email body
     * @return True if the email was sent successfully, false otherwise
     */
    public boolean sendEmail(String to, String subject, String body) {
        try {
            // Get email configuration from AppConfig
            String host = AppConfig.getSmtpHost();
            String port = AppConfig.getSmtpPort();
            String username = AppConfig.getEmailUsername();
            String password = AppConfig.getEmailPassword();
            
            // Set up mail server properties
            Properties properties = new Properties();
            properties.put("mail.smtp.host", host);
            properties.put("mail.smtp.port", port);
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            
            // Create a session with authentication
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password);
                }
            });
            
            // Create a message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            
            // Send the message
            Transport.send(message);
            
            // Log the notification
            logNotification(to, subject, body, "email");
            
            logger.info("Email sent successfully to: " + to);
            return true;
        } catch (MessagingException e) {
            logger.log(Level.SEVERE, "Error sending email", e);
            return false;
        }
    }
    
    /**
     * Send an SMS notification (simulated)
     * @param phoneNumber The recipient phone number
     * @param message The SMS message
     * @return True if the SMS was sent successfully, false otherwise
     */
    public boolean sendSms(String phoneNumber, String message) {
        try {
            // In a real implementation, this would use an SMS gateway API
            // For now, we'll just log the SMS
            logger.info("Simulating SMS to " + phoneNumber + ": " + message);
            
            // Log the notification
            logSms(phoneNumber, message);
            
            return true;
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error sending SMS", e);
            return false;
        }
    }
    
    /**
     * Send an appointment confirmation notification
     * @param appointment The appointment
     * @param client The client
     * @param pet The pet
     * @return True if the notification was sent successfully, false otherwise
     */
    public boolean sendAppointmentConfirmation(Appointment appointment, Client client, Pet pet) {
        String subject = "Appointment Confirmation - Pet Place Veterinary Clinic";
        String body = "Dear " + client.getName() + ",\n\n" +
                "This is to confirm your appointment for " + pet.getName() + " at Pet Place Veterinary Clinic.\n\n" +
                "Service: " + appointment.getService() + "\n" +
                "Date and Time: " + appointment.getFormattedStartTime() + "\n" +
                "Expected Duration: " + appointment.getServiceType().getDurationMinutes() + " minutes\n\n" +
                "If you need to reschedule or cancel, please contact us at least 2 hours before your appointment.\n\n" +
                "Thank you for choosing Pet Place Veterinary Clinic.\n\n" +
                "Best regards,\n" +
                "The Pet Place Team";
        
        return sendEmail(client.getEmail(), subject, body);
    }
    
    /**
     * Send an appointment reminder notification
     * @param appointment The appointment
     * @param client The client
     * @param pet The pet
     * @return True if the notification was sent successfully, false otherwise
     */
    public boolean sendAppointmentReminder(Appointment appointment, Client client, Pet pet) {
        String subject = "Appointment Reminder - Pet Place Veterinary Clinic";
        String body = "Dear " + client.getName() + ",\n\n" +
                "This is a reminder of your upcoming appointment for " + pet.getName() + " at Pet Place Veterinary Clinic.\n\n" +
                "Service: " + appointment.getService() + "\n" +
                "Date and Time: " + appointment.getFormattedStartTime() + "\n\n" +
                "If you need to reschedule or cancel, please contact us as soon as possible.\n\n" +
                "Thank you for choosing Pet Place Veterinary Clinic.\n\n" +
                "Best regards,\n" +
                "The Pet Place Team";
        
        return sendEmail(client.getEmail(), subject, body);
    }
    
    /**
     * Send a vaccination reminder notification
     * @param client The client
     * @param pet The pet
     * @param vaccineName The vaccine name
     * @param dueDate The due date
     * @return True if the notification was sent successfully, false otherwise
     */
    public boolean sendVaccinationReminder(Client client, Pet pet, String vaccineName, LocalDateTime dueDate) {
        String subject = "Vaccination Reminder - Pet Place Veterinary Clinic";
        String body = "Dear " + client.getName() + ",\n\n" +
                "This is a reminder that " + pet.getName() + " is due for a " + vaccineName + " vaccination.\n\n" +
                "Please contact us to schedule an appointment.\n\n" +
                "Thank you for choosing Pet Place Veterinary Clinic.\n\n" +
                "Best regards,\n" +
                "The Pet Place Team";
        
        return sendEmail(client.getEmail(), subject, body);
    }
    
    /**
     * Log a notification in the database
     * @param recipient The recipient
     * @param subject The subject
     * @param message The message
     * @param type The notification type
     */
    private void logNotification(String recipient, String subject, String message, String type) {
        String query = "INSERT INTO notification_log (reference_id, type, message, sent_at) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, 0); // No specific reference ID
            stmt.setString(2, type);
            stmt.setString(3, subject + ": " + message);
            stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error logging notification", e);
        }
    }
    
    /**
     * Log an SMS in the database
     * @param phoneNumber The phone number
     * @param message The message
     */
    private void logSms(String phoneNumber, String message) {
        String query = "INSERT INTO sms_log (phone_number, message, sent_at) VALUES (?, ?, ?)";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, phoneNumber);
            stmt.setString(2, message);
            stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error logging SMS", e);
        }
    }
    
    /**
     * Save a notification in the database
     * @param notification The notification to save
     * @throws DatabaseException If there is a database error
     */
    public void saveNotification(Notification notification) throws DatabaseException {
        String query = "INSERT INTO notifications (client_id, message, sent_time) VALUES (?, ?, ?)";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, notification.getClientId());
            stmt.setString(2, notification.getMessage());
            stmt.setTimestamp(3, Timestamp.valueOf(notification.getSentTime()));
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    notification.setNotificationId(rs.getInt(1));
                    logger.info("Saved notification with ID: " + notification.getNotificationId());
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error saving notification", e);
            throw new DatabaseException("Error saving notification: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get notifications for a client
     * @param clientId The client ID
     * @return List of notifications for the client
     * @throws DatabaseException If there is a database error
     */
    public List<Notification> getNotificationsByClientId(int clientId) throws DatabaseException {
        String query = "SELECT * FROM notifications WHERE client_id = ? ORDER BY sent_time DESC";
        List<Notification> notifications = new ArrayList<>();
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, clientId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    notifications.add(mapResultSetToNotification(rs));
                }
            }
            
            logger.info("Retrieved " + notifications.size() + " notifications for client ID: " + clientId);
            return notifications;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving notifications for client", e);
            throw new DatabaseException("Error retrieving notifications: " + e.getMessage(), e);
        }
    }
    
    /**
     * Map a ResultSet to a Notification object
     * @param rs The ResultSet
     * @return The Notification object
     * @throws SQLException If there is a database error
     */
    private Notification mapResultSetToNotification(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setNotificationId(rs.getInt("notification_id"));
        notification.setClientId(rs.getInt("client_id"));
        notification.setMessage(rs.getString("message"));
        notification.setSentTime(rs.getTimestamp("sent_time").toLocalDateTime());
        return notification;
    }
}
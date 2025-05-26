package vet.model;

import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Represents a notification sent to a client
 */
public class Notification {
    private int notificationId;
    private int clientId;
    private String message;
    private Date sentTime;
    private NotificationType type;
    private boolean delivered;
    private String referenceId;

    /**
     * Default constructor
     */
    public Notification() {
        this.sentTime = new Date();
        this.delivered = false;
    }

    /**
     * Constructor with essential fields
     * @param clientId The ID of the client
     * @param message The notification message
     * @param type The notification type
     */
    public Notification(int clientId, String message, NotificationType type) {
        this.clientId = clientId;
        this.message = message;
        this.type = type;
        this.sentTime = new Date();
        this.delivered = false;
    }

    /**
     * Get the notification ID
     * @return The notification ID
     */
    public int getNotificationId() {
        return notificationId;
    }

    /**
     * Set the notification ID
     * @param notificationId The notification ID
     */
    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
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
     * Get the message
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the message
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get the sent time
     * @return The sent time
     */
    public Date getSentTime() {
        return sentTime;
    }

    /**
     * Set the sent time
     * @param sentTime The sent time
     */
    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }

    /**
     * Get the notification type
     * @return The notification type
     */
    public NotificationType getType() {
        return type;
    }

    /**
     * Set the notification type
     * @param type The notification type
     */
    public void setType(NotificationType type) {
        this.type = type;
    }

    /**
     * Check if the notification has been delivered
     * @return true if the notification has been delivered, false otherwise
     */
    public boolean isDelivered() {
        return delivered;
    }

    /**
     * Set whether the notification has been delivered
     * @param delivered true if the notification has been delivered, false otherwise
     */
    public void setDelivered(boolean delivered) {
        this.delivered = delivered;
    }

    /**
     * Get the reference ID
     * @return The reference ID
     */
    public String getReferenceId() {
        return referenceId;
    }

    /**
     * Set the reference ID
     * @param referenceId The reference ID
     */
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    
    /**
     * Format the sent time as a string
     * @return The formatted sent time
     */
    public String getFormattedSentTime() {
        if (sentTime == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return dateFormat.format(sentTime);
    }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", clientId=" + clientId +
                ", type=" + type +
                ", sentTime=" + getFormattedSentTime() +
                ", delivered=" + delivered +
                '}';
    }
    
    /**
     * Enum representing notification types
     */
    public enum NotificationType {
        EMAIL("Email"),
        SMS("SMS"),
        APP("App Notification"),
        SYSTEM("System Message");
        
        private final String description;
        
        /**
         * Constructor
         * @param description The notification type description
         */
        NotificationType(String description) {
            this.description = description;
        }
        
        /**
         * Get the notification type description
         * @return The notification type description
         */
        public String getDescription() {
            return description;
        }
    }
}
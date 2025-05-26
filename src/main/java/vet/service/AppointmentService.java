package vet.service;

import vet.model.Appointment;
import vet.model.ServiceType;
import vet.util.ConnectionPool;
import vet.util.LoggerUtil;
import vet.exception.DatabaseException;
import vet.exception.ValidationException;

/**
 * Service class for managing appointments
 */
public class AppointmentService {
    private static final Logger logger = LoggerUtil.getLogger(AppointmentService.class);
    
    /**
     * Schedule a new appointment
     * @param appointment The appointment to schedule
     * @throws DatabaseException If there is a database error
     * @throws ValidationException If the appointment data is invalid
     */
    public void scheduleAppointment(Appointment appointment) throws DatabaseException, ValidationException {
        try {
            validateAppointment(appointment);
            
            String query = "INSERT INTO appointments (pet_id, service_type, start_time, end_time, status, notes, price) " +
                          "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            try (Connection conn = ConnectionPool.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                
                stmt.setInt(1, appointment.getPetId());
                stmt.setString(2, appointment.getServiceType().name());
                stmt.setTimestamp(3, appointment.getStartTime());
                stmt.setTimestamp(4, appointment.getEndTime());
                stmt.setString(5, appointment.getStatus());
                stmt.setString(6, appointment.getNotes());
                stmt.setDouble(7, appointment.getPrice());
                
                stmt.executeUpdate();
                
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        appointment.setAppointmentId(rs.getInt(1));
                        logger.info("Scheduled appointment with ID: " + appointment.getAppointmentId());
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error scheduling appointment", e);
            throw new DatabaseException("Error scheduling appointment: " + e.getMessage(), e);
        }
    }

    /**
     * Get appointments by date
     * @param date The date to search for
     * @return List of appointments on the specified date
     * @throws DatabaseException If there is a database error
     */
    public List<Appointment> getAppointmentsByDate(Date date) throws DatabaseException {
        String query = "SELECT * FROM appointments WHERE DATE(start_time) = ?";
        List<Appointment> appointments = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            logger.info("Retrieved " + appointments.size() + " appointments for date: " + date);
            return appointments;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving appointments by date", e);
            throw new DatabaseException("Error retrieving appointments: " + e.getMessage(), e);
        }
    }

    /**
     * Get appointments by client ID
     * @param clientId The client ID
     * @return List of appointments for the specified client
     * @throws DatabaseException If there is a database error
     */
    public List<Appointment> getAppointmentsByClientId(int clientId) throws DatabaseException {
        String query = "SELECT a.* FROM appointments a " +
                      "JOIN pets p ON a.pet_id = p.pet_id " +
                      "WHERE p.client_id = ?";
        List<Appointment> appointments = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            logger.info("Retrieved " + appointments.size() + " appointments for client ID: " + clientId);
            return appointments;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving appointments by client ID", e);
            throw new DatabaseException("Error retrieving appointments: " + e.getMessage(), e);
        }
    }

    /**
     * Get appointments by pet ID
     * @param petId The pet ID
     * @return List of appointments for the specified pet
     * @throws DatabaseException If there is a database error
     */
    public List<Appointment> getAppointmentsByPetId(int petId) throws DatabaseException {
        String query = "SELECT * FROM appointments WHERE pet_id = ?";
        List<Appointment> appointments = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, petId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            logger.info("Retrieved " + appointments.size() + " appointments for pet ID: " + petId);
            return appointments;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving appointments by pet ID", e);
            throw new DatabaseException("Error retrieving appointments: " + e.getMessage(), e);
        }
    }

    /**
     * Cancel an appointment
     * @param appointmentId The appointment ID
     * @throws DatabaseException If there is a database error
     */
    public void cancelAppointment(int appointmentId) throws DatabaseException {
        String query = "UPDATE appointments SET status = 'CANCELLED' WHERE appointment_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, appointmentId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Cancelled appointment with ID: " + appointmentId);
            } else {
                logger.warning("No appointment found with ID: " + appointmentId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error cancelling appointment", e);
            throw new DatabaseException("Error cancelling appointment: " + e.getMessage(), e);
        }
    }
    
    /**
     * Update an appointment's status
     * @param appointmentId The appointment ID
     * @param status The new status
     * @throws DatabaseException If there is a database error
     */
    public void updateAppointmentStatus(int appointmentId, String status) throws DatabaseException {
        String query = "UPDATE appointments SET status = ? WHERE appointment_id = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                logger.info("Updated appointment " + appointmentId + " status to: " + status);
            } else {
                logger.warning("No appointment found with ID: " + appointmentId);
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error updating appointment status", e);
            throw new DatabaseException("Error updating appointment status: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get upcoming appointments
     * @param days Number of days to look ahead
     * @return List of upcoming appointments
     * @throws DatabaseException If there is a database error
     */
    public List<Appointment> getUpcomingAppointments(int days) throws DatabaseException {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(days);
        
        String query = "SELECT * FROM appointments WHERE DATE(start_time) BETWEEN ? AND ? AND status != 'CANCELLED' ORDER BY start_time";
        List<Appointment> appointments = new ArrayList<>();

        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, java.sql.Date.valueOf(today));
            stmt.setDate(2, java.sql.Date.valueOf(endDate));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
            logger.info("Retrieved " + appointments.size() + " upcoming appointments");
            return appointments;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving upcoming appointments", e);
            throw new DatabaseException("Error retrieving upcoming appointments: " + e.getMessage(), e);
        }
    }

    /**
     * Validate appointment data
     * @param appointment The appointment to validate
     * @throws ValidationException If the appointment data is invalid
     * @throws DatabaseException If there is a database error
     */
    private void validateAppointment(Appointment appointment) throws ValidationException, DatabaseException {
        if (appointment.getPetId() <= 0) {
            throw new ValidationException("Pet ID is required");
        }
        
        if (appointment.getServiceType() == null) {
            throw new ValidationException("Service type is required");
        }
        
        if (appointment.getStartTime() == null) {
            throw new ValidationException("Start time is required");
        }
        
        if (appointment.getEndTime() == null) {
            throw new ValidationException("End time is required");
        }
        
        // Validate business hours
        validateBusinessHours(appointment.getStartTime());
        
        // Validate time slot availability
        validateTimeSlotAvailability(appointment);
        
        // Validate service-specific requirements
        switch (appointment.getServiceType()) {
            case VACCINATION:
                checkVaccineAvailability();
                break;
            case CONSULTATION:
            case SURGERY:
            case EMERGENCY:
                checkVetAvailability(appointment.getStartTime());
                break;
            default:
                // No special validation for other service types
                break;
        }
    }

    /**
     * Validate business hours
     * @param startTime The appointment start time
     * @throws ValidationException If the time is outside business hours
     */
    private void validateBusinessHours(Timestamp startTime) throws ValidationException {
        int hour = startTime.toLocalDateTime().getHour();
        if (hour < 8 || hour >= 18) {
            throw new ValidationException("Appointments are only available between 8:00 and 18:00");
        }
    }

    /**
     * Validate time slot availability
     * @param appointment The appointment to validate
     * @throws ValidationException If the time slot is not available
     * @throws DatabaseException If there is a database error
     */
    private void validateTimeSlotAvailability(Appointment appointment) throws ValidationException, DatabaseException {
        String query = "SELECT COUNT(*) FROM appointments WHERE start_time = ? AND status != 'CANCELLED'";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setTimestamp(1, appointment.getStartTime());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) >= 3) {
                throw new ValidationException("This time slot is fully booked");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error validating time slot", e);
            throw new DatabaseException("Error validating time slot: " + e.getMessage(), e);
        }
    }

    /**
     * Check vaccine availability
     * @throws ValidationException If no vaccines are available
     * @throws DatabaseException If there is a database error
     */
    private void checkVaccineAvailability() throws ValidationException, DatabaseException {
        String query = "SELECT COUNT(*) FROM vaccine_stock WHERE quantity > 0";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (!rs.next() || rs.getInt(1) == 0) {
                throw new ValidationException("No vaccines are currently available");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking vaccine availability", e);
            throw new DatabaseException("Error checking vaccine availability: " + e.getMessage(), e);
        }
    }

    /**
     * Check veterinarian availability
     * @param startTime The appointment start time
     * @throws ValidationException If no veterinarians are available
     * @throws DatabaseException If there is a database error
     */
    private void checkVetAvailability(Timestamp startTime) throws ValidationException, DatabaseException {
        String query = "SELECT COUNT(*) FROM appointments WHERE service_type IN ('CONSULTATION', 'SURGERY', 'EMERGENCY') " +
                      "AND start_time = ? AND status != 'CANCELLED'";
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, startTime);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) >= 2) {
                throw new ValidationException("No veterinarians are available at this time");
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error checking veterinarian availability", e);
            throw new DatabaseException("Error checking veterinarian availability: " + e.getMessage(), e);
        }
    }

    /**
     * Map a ResultSet to an Appointment object
     * @param rs The ResultSet
     * @return The Appointment object
     * @throws SQLException If there is a database error
     */
    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setPetId(rs.getInt("pet_id"));
        appointment.setServiceType(ServiceType.valueOf(rs.getString("service_type")));
        appointment.setStartTime(rs.getTimestamp("start_time"));
        appointment.setEndTime(rs.getTimestamp("end_time"));
        appointment.setStatus(rs.getString("status"));
        appointment.setNotes(rs.getString("notes"));
        appointment.setPrice(rs.getDouble("price"));
        return appointment;
    }
}
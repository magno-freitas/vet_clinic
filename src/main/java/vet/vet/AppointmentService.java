package vet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import model.Appointment;
import vet.ServiceType;

import vet.DatabaseConnection;

public class AppointmentService {
    
    public void scheduleAppointment(Appointment appointment) throws SQLException {
        validateAppointment(appointment);
        
        String query = "INSERT INTO appointments (pet_id, service, service_type, start_time, end_time, status, notes) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, appointment.getPetId());
            stmt.setString(2, appointment.getService());
            stmt.setString(3, appointment.getServiceType().name());
            stmt.setTimestamp(4, appointment.getStartTime());
            stmt.setTimestamp(5, appointment.getEndTime());
            stmt.setString(6, appointment.getStatus());
            stmt.setString(7, appointment.getNotes());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    appointment.setAppointmentId(rs.getInt(1));
                }
            }
        }
    }

    public List<Appointment> getAppointmentsByDate(Date date) throws SQLException {
        String query = "SELECT * FROM appointments WHERE DATE(start_time) = ?";
        List<Appointment> appointments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        }
        return appointments;
    }

    private void validateAppointment(Appointment appointment) throws SQLException {
        if (appointment.getServiceType() == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }
        
        switch (appointment.getServiceType()) {
            case BANHO:
            case TOSA:
                validateBusinessHours(appointment.getStartTime());
                break;
            case VACINA:
                checkVaccineAvailability();
                break;
            case CONSULTA:
                checkVetAvailability(appointment.getStartTime());
                break;
        }
        
        validateTimeSlot(appointment);
    }

    private void validateBusinessHours(Timestamp startTime) {
        int hour = startTime.toLocalDateTime().getHour();
        if (hour < 8 || hour >= 18) {
            throw new IllegalArgumentException("Appointments only available between 8:00 and 18:00");
        }
    }

    private void validateTimeSlot(Appointment appointment) throws SQLException {
        String query = "SELECT COUNT(*) FROM appointments WHERE start_time = ? AND status != 'cancelled'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setTimestamp(1, appointment.getStartTime());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) >= 3) {
                throw new SQLException("Time slot is fully booked");
            }
        }
    }

    private boolean checkVaccineAvailability() throws SQLException {
        String query = "SELECT COUNT(*) FROM vaccine_stock WHERE quantity > 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (!rs.next() || rs.getInt(1) == 0) {
                throw new SQLException("No vaccines available");
            }
            return true;
        }
    }

    private boolean checkVetAvailability(Timestamp startTime) throws SQLException {
        String query = "SELECT COUNT(*) FROM appointments WHERE service_type = 'CONSULTA' AND start_time = ? AND status != 'cancelled'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, startTime);
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) >= 2) {
                throw new SQLException("No veterinarians available at this time");
            }
            return true;
        }
    }

    private Appointment mapResultSetToAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(rs.getInt("appointment_id"));
        appointment.setPetId(rs.getInt("pet_id"));
        appointment.setService(rs.getString("service"));
        appointment.setServiceType(ServiceType.valueOf(rs.getString("service_type")));
        appointment.setStartTime(rs.getTimestamp("start_time"));
        appointment.setEndTime(rs.getTimestamp("end_time"));
        appointment.setStatus(rs.getString("status"));
        appointment.setNotes(rs.getString("notes"));
        return appointment;
    }


    public List<Appointment> getAppointmentsByClientId(int clientId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAppointmentsByClientId'");
    }

    public void cancelAppointment(int appointmentId) throws SQLException {
        String query = "UPDATE appointments SET status = 'cancelled' WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, appointmentId);
            stmt.executeUpdate();
        }

    }
}
package vet;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentService {
    
    public void scheduleAppointment(Appointment appointment) throws SQLException {
        validateAppointment(appointment);
        
        String query = "INSERT INTO appointments (pet_id, service, service_type, start_time, end_time, status, notes) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
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
    
    private void validateAppointment(Appointment appointment) throws SQLException {
        if (appointment.getServiceType() == null) {
            throw new IllegalArgumentException("Tipo de serviço não pode ser nulo");
        }
        
        switch (appointment.getServiceType()) {
            case BANHO:
            case TOSA:
                validateBusinessHours(appointment.getStartTime());
                break;
            case VACINA:
                if (!checkVaccineAvailability()) {
                    throw new SQLException("Não há vacinas disponíveis no momento");
                }
                break;
            case CONSULTA:
                if (!checkVetAvailability(appointment.getStartTime())) {
                    throw new SQLException("Não há veterinários disponíveis neste horário");
                }
                break;
        }
        
        validateTimeSlot(appointment);
    }
    
    private void validateTimeSlot(Appointment appointment) throws SQLException {
        String query = "SELECT COUNT(*) FROM appointments WHERE start_time = ? AND status != 'cancelado'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setTimestamp(1, appointment.getStartTime());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) >= 3) { // Máximo 3 agendamentos simultâneos
                throw new SQLException("Horário já está totalmente ocupado");
            }
        }
    }
    
    private boolean checkVaccineAvailability() throws SQLException {
        String query = "SELECT COUNT(*) FROM vaccine_stock WHERE quantity > 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) > 0;
        }
    }
    
    private boolean checkVetAvailability(Timestamp startTime) throws SQLException {
        String query = "SELECT COUNT(*) FROM appointments WHERE service_type = 'CONSULTA' AND start_time = ? AND status != 'cancelado'";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTimestamp(1, startTime);
            ResultSet rs = stmt.executeQuery();
            return rs.next() && rs.getInt(1) < 2; // Máximo 2 consultas simultâneas
        }
    }
    
    private void validateBusinessHours(Timestamp startTime) {
        int hour = startTime.toLocalDateTime().getHour();
        if (hour < 8 || hour >= 18) {
            throw new IllegalArgumentException("Horário fora do período de funcionamento (8h-18h)");
        }
    }

    public List<Appointment> getAppointmentsByClientId(int clientId) throws SQLException {
        String query = "SELECT a.* FROM appointments a " +
                      "JOIN pets p ON a.pet_id = p.pet_id " +
                      "WHERE p.client_id = ?";
        List<Appointment> appointments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, clientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        }
        return appointments;
    }

    public List<Appointment> getAppointmentsByDate(Date date) throws SQLException {
        String query = "SELECT * FROM appointments WHERE DATE(start_time) = ?";
        List<Appointment> appointments = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                appointments.add(mapResultSetToAppointment(rs));
            }
        }
        return appointments;
    }

    public void updateAppointment(Appointment appointment) throws SQLException {
        validateAppointment(appointment);
        
        String query = "UPDATE appointments SET pet_id = ?, service = ?, service_type = ?, " +
                      "start_time = ?, end_time = ?, status = ?, notes = ? WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, appointment.getPetId());
            stmt.setString(2, appointment.getService());
            stmt.setString(3, appointment.getServiceType().name());
            stmt.setTimestamp(4, appointment.getStartTime());
            stmt.setTimestamp(5, appointment.getEndTime());
            stmt.setString(6, appointment.getStatus());
            stmt.setString(7, appointment.getNotes());
            stmt.setInt(8, appointment.getAppointmentId());
            
            stmt.executeUpdate();
        }
    }

    public Appointment getAppointmentById(int appointmentId) throws SQLException {
        String query = "SELECT * FROM appointments WHERE appointment_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAppointment(rs);
            }
        }
        return null;
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
}
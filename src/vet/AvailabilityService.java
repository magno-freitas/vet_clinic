package vet;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class AvailabilityService {
    public List<Availability> getAvailableSlots(Date date) throws SQLException {
        String query = "SELECT * FROM availability WHERE date = ? AND is_available = TRUE";
        List<Availability> slots = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setDate(1, date);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Availability availability = new Availability();
                availability.setSlotId(rs.getInt("slot_id"));
                availability.setDate(rs.getDate("date"));
                
                // Combinar a data selecionada com os horários
                Calendar cal = Calendar.getInstance();
                
                // Para o horário de início
                cal.setTime(date);
                Timestamp startTime = rs.getTimestamp("start_time");
                Calendar startCal = Calendar.getInstance();
                startCal.setTimeInMillis(startTime.getTime());
                cal.set(Calendar.HOUR_OF_DAY, startCal.get(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, startCal.get(Calendar.MINUTE));
                cal.set(Calendar.SECOND, startCal.get(Calendar.SECOND));
                availability.setStartTime(new Timestamp(cal.getTimeInMillis()));
                
                // Para o horário de término
                cal.setTime(date);
                Timestamp endTime = rs.getTimestamp("end_time");
                Calendar endCal = Calendar.getInstance();
                endCal.setTimeInMillis(endTime.getTime());
                cal.set(Calendar.HOUR_OF_DAY, endCal.get(Calendar.HOUR_OF_DAY));
                cal.set(Calendar.MINUTE, endCal.get(Calendar.MINUTE));
                cal.set(Calendar.SECOND, endCal.get(Calendar.SECOND));
                availability.setEndTime(new Timestamp(cal.getTimeInMillis()));
                
                availability.setAvailable(rs.getBoolean("is_available"));
                slots.add(availability);
            }
        }

        return slots;
    }


    public void addSlot(Availability slot) throws SQLException {
        String query = "INSERT INTO availability (date, start_time, end_time, is_available) " +
                      "VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS)) {
            
            stmt.setDate(1, slot.getDate());
            stmt.setTimestamp(2, slot.getStartTime());
            stmt.setTimestamp(3, slot.getEndTime());
            stmt.setBoolean(4, true);
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    slot.setSlotId(rs.getInt(1));
                }
            }
        }
    }
    public void bookSlot(int slotId) throws SQLException {
        String query = "UPDATE availability SET is_available = FALSE WHERE slot_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, slotId);
            stmt.executeUpdate();
        }
    }

    
}
package vet.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import vet.util.ConnectionPool;
import vet.util.LoggerUtil;
import vet.exception.DatabaseException;

public class AvailabilityService {
    private static final Logger logger = LoggerUtil.getLogger(AvailabilityService.class);
    
    public List<Date> getAvailableSlots(java.sql.Date date) throws SQLException {
        List<Date> availableSlots = new ArrayList<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 8); // Start at 8 AM
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        // Get booked slots
        List<Date> bookedSlots = getBookedSlots(date);
        
        // Generate available slots every 30 minutes from 8 AM to 6 PM
        while (cal.get(Calendar.HOUR_OF_DAY) < 18) { // Until 6 PM
            Date timeSlot = cal.getTime();
            if (!isSlotBooked(timeSlot, bookedSlots)) {
                availableSlots.add(timeSlot);
            }
            cal.add(Calendar.MINUTE, 30);
        }
        
        return availableSlots;
    }
    
    private List<Date> getBookedSlots(java.sql.Date date) throws SQLException {
        List<Date> bookedSlots = new ArrayList<>();
        String sql = "SELECT start_time, end_time FROM appointments WHERE DATE(start_time) = ?";
        
        try (Connection conn = ConnectionPool.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, date);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Timestamp startTime = rs.getTimestamp("start_time");
                    Timestamp endTime = rs.getTimestamp("end_time");
                    
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(startTime.getTime());
                    
                    // Add all 30-minute slots between start and end time
                    while (cal.getTimeInMillis() < endTime.getTime()) {
                        bookedSlots.add(cal.getTime());
                        cal.add(Calendar.MINUTE, 30);
                    }
                }
            }
        } catch (SQLException e) {
            logger.severe("Error getting booked slots: " + e.getMessage());
            throw new DatabaseException("Error getting booked slots", e);
        }
        
        return bookedSlots;
    }
    
    private boolean isSlotBooked(Date slot, List<Date> bookedSlots) {
        for (Date bookedSlot : bookedSlots) {
            if (Math.abs(slot.getTime() - bookedSlot.getTime()) < 30 * 60 * 1000) { // Within 30 minutes
                return true;
            }
        }
        return false;
    }
}
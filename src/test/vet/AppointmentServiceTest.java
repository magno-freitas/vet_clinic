package test.vet;

import vet.*;
import vet.exception.VetClinicException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.Date;

public class AppointmentServiceTest {
    private AppointmentService appointmentService;
    
    @BeforeEach
    void setUp() {
        appointmentService = new AppointmentService();
    }
    
    @Test
    void testScheduleAppointment_Success() {
        Appointment appointment = createValidAppointment();
        assertDoesNotThrow(() -> appointmentService.scheduleAppointment(appointment));
    }
    
    @Test
    void testScheduleAppointment_NullAppointment() {
        assertThrows(VetClinicException.class, 
            () -> appointmentService.scheduleAppointment(null));
    }
    
    @Test
    void testScheduleAppointment_OutsideBusinessHours() {
        Appointment appointment = createValidAppointment();
        appointment.setStartTime(new Timestamp(System.currentTimeMillis()));
        appointment.getStartTime().setHours(20); // Outside business hours
        
        assertThrows(VetClinicException.class, 
            () -> appointmentService.scheduleAppointment(appointment));
    }
    
    @Test
    void testGetAppointmentsByDate_Success() {
        Date date = new Date();
        List<Appointment> appointments = 
            assertDoesNotThrow(() -> appointmentService.getAppointmentsByDate(date));
        assertNotNull(appointments);
    }
    
    @Test
    void testGetAppointmentsByDate_NullDate() {
        assertThrows(VetClinicException.class, 
            () -> appointmentService.getAppointmentsByDate(null));
    }
    
    private Appointment createValidAppointment() {
        Appointment appointment = new Appointment();
        appointment.setPetId(1);
        appointment.setServiceType("REGULAR_CHECKUP");
        
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timestamp.setHours(10); // Within business hours
        appointment.setStartTime(timestamp);
        appointment.setStatus("SCHEDULED");
        
        return appointment;
    }
}
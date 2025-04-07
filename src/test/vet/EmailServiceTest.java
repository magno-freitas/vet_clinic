package test.vet;

import vet.*;
import vet.exception.VetClinicException;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class EmailServiceTest {
    
    @Test
    void testSendEmail_Success() {
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Message";
        
        assertDoesNotThrow(() -> 
            EmailService.sendEmail(to, subject, text));
    }
    
    @Test
    void testSendEmail_InvalidEmail() {
        String to = "invalid-email";
        String subject = "Test Subject";
        String text = "Test Message";
        
        assertThrows(VetClinicException.class, () -> 
            EmailService.sendEmail(to, subject, text));
    }
    
    @Test
    void testSendEmail_NullParameters() {
        assertThrows(VetClinicException.class, () -> 
            EmailService.sendEmail(null, "subject", "text"));
        
        assertThrows(VetClinicException.class, () -> 
            EmailService.sendEmail("test@example.com", null, "text"));
        
        assertThrows(VetClinicException.class, () -> 
            EmailService.sendEmail("test@example.com", "subject", null));
    }
}
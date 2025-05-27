package vet.ui;

import ui.*;
import vet.util.LoggerUtil;
import vet.exception.VetClinicException;
import vet.model.Appointment;
import vet.service.AppointmentService;
import vet.service.ClientService;
import java.util.logging.Logger;
import java.util.logging.Level;


import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;


public class AppointmentStatusUI {
    private final Scanner scanner;
    private final vet.service.ClientService clientService;
    private final vet.service.AppointmentService appointmentService;

    public AppointmentStatusUI(Scanner scanner, ClientService clientService, 
                             AppointmentService appointmentService) {
        this.scanner = scanner;
        this.clientService = clientService;
        this.appointmentService = appointmentService;
    }

    public void check() throws SQLException {
        System.out.println("\n=== Verificar Status de Consulta ===");
        
        try {
            // Get client email
            System.out.print("Email do Cliente: ");
            String email = scanner.nextLine();
            
            // Get client phone
            System.out.print("Telefone do Cliente: ");
            String phone = scanner.nextLine();
            
            // Get client ID
            int clientId = clientService.getClientId(email, phone);
            if (clientId == -1) {
                throw new VetClinicException("Cliente não encontrado");
            }
            
            // Get appointments
            List<Appointment> appointments = appointmentService.getAppointmentsByClientId(clientId);
            if (appointments.isEmpty()) {
                System.out.println("Não há consultas agendadas para este cliente.");
                return;
            }
            
            // Display appointments
            System.out.println("\nConsultas Agendadas:");
            for (Appointment appointment : appointments) {
                System.out.printf("ID: %d | Data: %s | Status: %s | Tipo: %s%n",
                    appointment.getId(),
                    appointment.getStartTime(),
                    appointment.getStatus(),
                    appointment.getServiceType());
            }
            
       
    
        } catch (VetClinicException e) {
            System.out.println("Erro: " + e.getMessage());
            LoggerUtil.logError("Error checking appointment status", e);
        }
    }
}
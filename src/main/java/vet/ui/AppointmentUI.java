

import ui.*;
import util.ValidationUtil;
import util.LoggerUtil;
import exception.VetClinicException;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.List;
import java.util.Date;

public class AppointmentUI {
    private final Scanner scanner;
    private final PetService petService;
    private final AppointmentService appointmentService;
    private final AvailabilityService availabilityService;

    public AppointmentUI(Scanner scanner, PetService petService, 
                        AppointmentService appointmentService,
                        AvailabilityService availabilityService) {
        this.scanner = scanner;
        this.petService = petService;
        this.appointmentService = appointmentService;
        this.availabilityService = availabilityService;
    }

    public void schedule() throws SQLException {
        System.out.println("\n=== Agendamento de Consulta ===");
        
        try {
            // Get pet ID
            System.out.print("ID do Pet: ");
            int petId = Integer.parseInt(scanner.nextLine());
            
            // Validate pet exists
            Pet pet = petService.getPetById(petId);
            if (pet == null) {
                throw new VetClinicException("Pet not found");
            }

            // Get service type
            System.out.println("Tipos de Serviço:");
            System.out.println("1. Consulta Regular");
            System.out.println("2. Vacinação");
            System.out.println("3. Cirurgia");
            System.out.print("Escolha o tipo de serviço: ");
            String service = getServiceType(scanner.nextLine());

            // Get appointment date
            Date date = requestValidDate();
            ValidationUtil.validateFutureDate(date);

            // Check availability
            List<Availability> availableSlots = availabilityService.getAvailableSlots(date);
            if (availableSlots.isEmpty()) {
                System.out.println("Não há horários disponíveis para esta data.");
                return;
            }

            // Show available slots
            System.out.println("\nHorários Disponíveis:");
            for (int i = 0; i < availableSlots.size(); i++) {
                System.out.println((i + 1) + ". " + availableSlots.get(i).getStartTime());
            }

            // Get selected slot
            System.out.print("Escolha um horário: ");
            int slotChoice = Integer.parseInt(scanner.nextLine()) - 1;
            if (slotChoice < 0 || slotChoice >= availableSlots.size()) {
                throw new VetClinicException("Invalid slot selection");
            }

            Availability selectedSlot = availableSlots.get(slotChoice);
            Appointment appointment = new Appointment();
            appointment.setPetId(petId);
            appointment.setServiceType(service);
            appointment.setStartTime(selectedSlot.getStartTime());
            appointment.setStatus("SCHEDULED");

            appointmentService.scheduleAppointment(appointment);
            System.out.println("Consulta agendada com sucesso!");
            
        } catch (NumberFormatException e) {
            throw new VetClinicException("Invalid number format", e);
        } catch (VetClinicException e) {
            System.out.println("Erro no agendamento: " + e.getMessage());
            LoggerUtil.logError("Appointment scheduling error", e);
        }
    }

    private String getServiceType(String choice) {
        switch (choice) {
            case "1": return "REGULAR_CHECKUP";
            case "2": return "VACCINATION";
            case "3": return "SURGERY";
            default: throw new VetClinicException("Invalid service type");
        }
    }

    private Date requestValidDate() {
        while (true) {
            try {
                System.out.print("Data da Consulta (dd/mm/yyyy): ");
                String dateStr = scanner.nextLine();
                return new java.text.SimpleDateFormat("dd/MM/yyyy").parse(dateStr);
            } catch (java.text.ParseException e) {
                System.out.println("Formato de data inválido. Use dd/mm/yyyy");
            }
        }
    }
}
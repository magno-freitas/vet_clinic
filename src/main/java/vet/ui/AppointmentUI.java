package ui;

import vet.model.Appointment;
import vet.model.ServiceType;
import vet.model.Pet;
import vet.service.AppointmentService;
import vet.service.AvailabilityService;
import vet.service.PetService;
import vet.util.LoggerUtil;
import vet.util.ValidationUtil;
import vet.exception.VetClinicException;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class AppointmentUI {
    private static final Logger logger = LoggerUtil.getLogger(AppointmentUI.class);
    private final Scanner scanner;
    private final PetService petService;
    private final AppointmentService appointmentService;
    private final AvailabilityService availabilityService;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public AppointmentUI(Scanner scanner, PetService petService, 
                        AppointmentService appointmentService,
                        AvailabilityService availabilityService) {
        this.scanner = scanner;
        this.petService = petService;
        this.appointmentService = appointmentService;
        this.availabilityService = availabilityService;
    }

    public AppointmentUI(Scanner scanner2, service.PetService petService2,
            service.AppointmentService appointmentService2, service.AvailabilityService availabilityService2) {
        //TODO Auto-generated constructor stub
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

    public void schedule() {
        try {
            System.out.println("\n=== Agendar Consulta ===");
            
            // Get pet ID
            System.out.print("Digite o ID do pet: ");
            int petId = Integer.parseInt(scanner.nextLine());
            Pet pet = petService.getPetById(petId);
            
            if (pet == null) {
                throw new VetClinicException("Pet não encontrado");
            }

            // Select service type
            System.out.println("\nTipos de serviço disponíveis:");
            for (ServiceType type : ServiceType.values()) {
                System.out.printf("%s - %s (Duração: %d minutos, Preço: R$%.2f)%n", 
                    type.name(), type.getDescription(), type.getDurationMinutes(), type.getDefaultPrice());
            }
            
            System.out.print("\nEscolha o tipo de serviço: ");
            String serviceTypeStr = scanner.nextLine().toUpperCase();
            ServiceType serviceType = ServiceType.valueOf(serviceTypeStr);

            // Get date
            System.out.print("Digite a data (dd/MM/yyyy): ");
            String dateStr = scanner.nextLine();
            Date date = dateFormat.parse(dateStr);
            ValidationUtil.validateFutureDate(date);

            // Show available times
            List<Date> availableTimes = availabilityService.getAvailableSlots(new java.sql.Date(date.getTime()));
            
            if (availableTimes.isEmpty()) {
                throw new VetClinicException("Não há horários disponíveis para esta data");
            }

            System.out.println("\nHorários disponíveis:");
            int i = 1;
            for (Date time : availableTimes) {
                System.out.printf("%d - %s%n", i++, timeFormat.format(time));
            }

            // Select time
            System.out.print("\nEscolha o número do horário: ");
            int timeChoice = Integer.parseInt(scanner.nextLine()) - 1;
            Date selectedTime = availableTimes.get(timeChoice);

            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setPetId(petId);
            appointment.setServiceType(serviceType);
            appointment.setStartTime(new Timestamp(selectedTime.getTime()));
            
            Calendar cal = Calendar.getInstance();
            cal.setTime(selectedTime);
            cal.add(Calendar.MINUTE, serviceType.getDurationMinutes());
            appointment.setEndTime(new Timestamp(cal.getTimeInMillis()));
            
            System.out.print("Observações adicionais: ");
            String notes = scanner.nextLine();
            appointment.setNotes(notes);
            appointment.setStatus("AGENDADO");

            // Save appointment
            appointmentService.scheduleAppointment(appointment);
            System.out.println("\nConsulta agendada com sucesso!");

        } catch (SQLException e) {
            logger.severe("Erro ao agendar consulta: " + e.getMessage());
            System.out.println("Erro ao agendar consulta. Por favor, tente novamente.");
        } catch (ParseException e) {
            System.out.println("Data inválida. Use o formato dd/MM/yyyy");
        } catch (IllegalArgumentException e) {
            System.out.println("Tipo de serviço inválido");
        } catch (VetClinicException e) {
            System.out.println(e.getMessage());
        } catch (Exception e) {
            logger.severe("Erro inesperado: " + e.getMessage());
            System.out.println("Ocorreu um erro inesperado. Por favor, tente novamente.");
        }
    }

    public void viewAppointments() {
        try {
            System.out.println("\n=== Consultar Agendamentos ===");
            
            System.out.print("Digite a data (dd/MM/yyyy): ");
            String dateStr = scanner.nextLine();
            Date date = dateFormat.parse(dateStr);

            List<Appointment> appointments = appointmentService.getAppointmentsByDate(date);
            
            if (appointments.isEmpty()) {
                System.out.println("Não há agendamentos para esta data.");
                return;
            }

            System.out.println("\nAgendamentos:");
            for (Appointment appointment : appointments) {
                Pet pet = petService.getPetById(appointment.getPetId());
                System.out.printf("ID: %d | Pet: %s | Serviço: %s | Horário: %s - %s | Status: %s%n",
                    appointment.getAppointmentId(),
                    pet.getName(),
                    appointment.getServiceType().getDescription(),
                    timeFormat.format(appointment.getStartTime()),
                    timeFormat.format(appointment.getEndTime()),
                    appointment.getStatus());
                
                if (appointment.getNotes() != null && !appointment.getNotes().isEmpty()) {
                    System.out.printf("Observações: %s%n", appointment.getNotes());
                }
                System.out.println();
            }

        } catch (SQLException e) {
            logger.severe("Erro ao consultar agendamentos: " + e.getMessage());
            System.out.println("Erro ao consultar agendamentos. Por favor, tente novamente.");
        } catch (ParseException e) {
            System.out.println("Data inválida. Use o formato dd/MM/yyyy");
        } catch (Exception e) {
            logger.severe("Erro inesperado: " + e.getMessage());
            System.out.println("Ocorreu um erro inesperado. Por favor, tente novamente.");
        }
    }
}
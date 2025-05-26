package vet;

import vet.config.AppConfig;
import vet.model.ServiceType;
import vet.model.Appointment;
import vet.model.Availability;
import vet.model.Client;
import vet.model.Pet;
import vet.service.*;
import vet.ui.MainFrame;
import vet.util.ConnectionPool;
import vet.util.LoggerUtil;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;

/**
 * Main entry point for the Veterinary Clinic Application
 * Supports both GUI and Console modes
 */
public class Main {
    private static final Logger logger = LoggerUtil.getLogger(Main.class);

    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static boolean useGUI = true; // Set to false for console mode

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--console")) {
            useGUI = false;
        }

        try {
            // Initialize configuration
            AppConfig.initialize();
            logger.info("Application configuration loaded successfully");
            
            // Initialize database connection pool
            ConnectionPool.initialize();
            logger.info("Database connection pool initialized");
            
            if (useGUI) {
                startGUIMode();
            } else {
                startConsoleMode();
            }

            // Add shutdown hook to clean up resources
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                ConnectionPool.closePool();
                logger.info("Application shutting down, resources cleaned up");
            }));
            
        } catch (Exception e) {
            logger.severe("Error starting application: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void startGUIMode() {
        SwingUtilities.invokeLater(() -> {
            try {
                MainFrame mainFrame = new MainFrame();
                mainFrame.setVisible(true);
                logger.info("Application UI started successfully");
            } catch (Exception e) {
                logger.severe("Error starting application UI: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    private static void startConsoleMode() {
        Scanner scanner = new Scanner(System.in);
        ClientService clientService = new ClientService();
        PetService petService = new PetService();
        AppointmentService appointmentService = new AppointmentService();
        AvailabilityService availabilityService = new AvailabilityService();

        while (true) {
            try {
                System.out.println("\n=== Sistema Veterinário ===");
                System.out.println("1. Cadastrar Cliente");
                System.out.println("2. Cadastrar Pet");
                System.out.println("3. Agendar Consulta");
                System.out.println("4. Listar Clientes");
                System.out.println("5. Listar Pets");
                System.out.println("6. Verificar Status de Consulta");
                System.out.println("7. Sair");
                System.out.print("Escolha uma opção: ");

                int opcao = Integer.parseInt(scanner.nextLine());

                switch (opcao) {
                    case 1:
                        cadastrarCliente(scanner, clientService);
                        break;
                    case 2:
                        cadastrarPet(scanner, petService, clientService);
                        break;
                    case 3:
                        agendarServico(scanner, petService, appointmentService, availabilityService);
                        break;
                    case 4:
                        listarClientes(clientService);
                        break;
                    case 5:
                        listarPets(petService);
                        break;
                    case 6:
                        verificarStatusConsulta(scanner, clientService, appointmentService);
                        break;
                    case 7:
                        System.out.println("Encerrando o programa...");
                        scanner.close();
                        return;
                    default:
                        System.out.println("Opção inválida!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido!");
            } catch (SQLException e) {
                System.out.println("Erro de banco de dados: " + e.getMessage());
                logger.severe("Erro de banco de dados: " + e.getMessage());
            }
        }
    }

    private static void verificarStatusConsulta(Scanner scanner, ClientService clientService, 
            AppointmentService appointmentService) throws SQLException {
        System.out.println("\n=== Verificar Status de Consulta ===");
        
        System.out.print("Digite seu ID de cliente: ");
        int clientId = Integer.parseInt(scanner.nextLine());
        
        // Verificar se o cliente existe
        Client client = clientService.getClientById(clientId);
        if (client == null) {
            System.out.println("Cliente não encontrado!");
            return;
        }
        
        // Buscar consultas do cliente
        List<Appointment> appointments = appointmentService.getAppointmentsByClientId(clientId);
        
        if (appointments == null || appointments.isEmpty()) {
            System.out.println("Não foram encontradas consultas para este cliente.");
            return;
        }
        
        System.out.println("\nConsultas encontradas para " + client.getName() + ":");
        System.out.println("----------------------------------------------------");
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        for (Appointment appointment : appointments) {
            System.out.println("ID da Consulta: " + appointment.getAppointmentId());
            System.out.println("Serviço: " + appointment.getService());
            System.out.println("Data/Hora: " + dateFormat.format(appointment.getStartTime()));
            System.out.println("Status: " + appointment.getStatus());
            System.out.println("Observações: " + (appointment.getNotes() != null ? appointment.getNotes() : "Nenhuma"));
            System.out.println("----------------------------------------------------");
        }
    }

    private static void agendarServico(Scanner scanner, PetService petService, 
            AppointmentService appointmentService, AvailabilityService availabilityService) throws SQLException {
        System.out.println("\n=== Agendamento de Serviço ===");
        
        // Mostrar lista de pets
        System.out.println("\nPets disponíveis:");
        listarPets(petService);
        
        System.out.print("\nDigite o ID do pet: ");
        int petId = Integer.parseInt(scanner.nextLine());
        
        // Verificar se o pet existe
        Pet pet = petService.getPetById(petId);
        if (pet == null) {
            System.out.println("Pet não encontrado!");
            return;
        }

        // Menu de serviços
        System.out.println("\nSelecione o tipo de serviço:");
        System.out.println("1. Banho");
        System.out.println("2. Tosa");
        System.out.println("3. Vacina");
        System.out.println("4. Consulta");
        
        System.out.print("Escolha um serviço: ");
        int serviceChoice = Integer.parseInt(scanner.nextLine());
        
        String service;
        ServiceType serviceType;
        
        switch (serviceChoice) {
            case 1:
                serviceType = ServiceType.BANHO;
                service = serviceType.getDescricao();
                break;
            case 2:
                serviceType = ServiceType.TOSA;
                service = serviceType.getDescricao();
                if (checkRecentService(petId, ServiceType.TOSA, appointmentService)) {
                    System.out.println("ALERTA: Este pet já realizou uma tosa nos últimos 30 dias.");
                    System.out.print("Deseja continuar mesmo assim? (S/N): ");
                    if (!scanner.nextLine().equalsIgnoreCase("S")) {
                        return;
                    }
                }
                break;
            case 3:
                serviceType = ServiceType.VACINA;
                service = serviceType.getDescricao();
                if (checkRecentService(petId, ServiceType.VACINA, appointmentService)) {
                    System.out.println("ALERTA: Este pet já tomou vacina nos últimos 30 dias.");
                    System.out.print("Deseja continuar mesmo assim? (S/N): ");
                    if (!scanner.nextLine().equalsIgnoreCase("S")) {
                        return;
                    }
                }
                break;
            case 4:
                serviceType = ServiceType.CONSULTA;
                service = serviceType.getDescricao();
                break;
            default:
                System.out.println("Opção inválida!");
                return;
        }

        Date date = requestValidDate(scanner);
        if (date == null) return;

        if (!checkDailyAvailability(date, appointmentService)) return;
        
        List<Availability> availableSlots = getAvailableSlots(date, availabilityService);
        if (availableSlots.isEmpty()) return;
        
        Availability selectedSlot = selectTimeSlot(scanner, availableSlots);
        if (selectedSlot == null) return;
        
        Appointment appointment = createAppointment(petId, service, selectedSlot);
        appointment.setServiceType(serviceType);
        
        appointmentService.scheduleAppointment(appointment);
        
        showAppointmentConfirmation(appointment, date);
    }

    private static boolean checkRecentService(int petId, ServiceType serviceType, 
            AppointmentService appointmentService) throws SQLException {
        return appointmentService.hasRecentService(petId, serviceType, 30);
    }

    private static void validateEmail(String email) {
        if (!email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }
    
    private static void validatePhone(String phone) {
        if (!phone.matches("^[0-9]{10,11}$")) {
            throw new IllegalArgumentException("Telefone inválido");
        }
    }
    
    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
    }
    
    private static void cadastrarCliente(Scanner scanner, ClientService clientService) {
        System.out.println("\n=== Cadastro de Cliente ===");
        
        try {
            System.out.print("Nome completo: ");
            String nome = scanner.nextLine();
            validateName(nome);
            
            System.out.print("Email: ");
            String email = scanner.nextLine();
            validateEmail(email);
            
            System.out.print("Telefone (apenas números): ");
            String telefone = scanner.nextLine();
            validatePhone(telefone);
            
            System.out.print("Endereço: ");
            String endereco = scanner.nextLine();
            
            Client cliente = new Client();
            cliente.setName(nome);
            cliente.setEmail(email);
            cliente.setPhone(telefone);
            cliente.setAddress(endereco);
            
            try {
                // Verificar se já existe cliente com mesmo email e telefone
                int existingClientId = clientService.getClientId(email, telefone);
                if (existingClientId != -1) {
                    System.out.println("ALERTA: Já existe um cliente com este email e telefone (ID: " + existingClientId + ")");
                    System.out.print("Deseja continuar mesmo assim? (S/N): ");
                    if (!scanner.nextLine().equalsIgnoreCase("S")) {
                        return;
                    }
                }
                
                clientService.addClient(cliente);
                System.out.println("Cliente cadastrado com sucesso! ID: " + cliente.getClientId());
                
                // Log de auditoria
                AuditLogService.logAction("Cliente cadastrado", "ID: " + cliente.getClientId() + ", Nome: " + cliente.getName());
                
            } catch (SQLException e) {
                System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
                logger.severe("Erro ao cadastrar cliente: " + e.getMessage());
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("Erro de validação: " + e.getMessage());
        }
    }
    
    private static void cadastrarPet(Scanner scanner, PetService petService, ClientService clientService) throws SQLException {
        System.out.println("\n=== Cadastro de Pet ===");
        
        // Mostrar lista de clientes
        System.out.println("\nClientes cadastrados:");
        listarClientes(clientService);
        
        System.out.print("\nDigite o ID do dono: ");
        int clientId = Integer.parseInt(scanner.nextLine());
        
        // Verificar se o cliente existe
        Client client = clientService.getClientById(clientId);
        if (client == null) {
            System.out.println("Cliente não encontrado!");
            return;
        }
        
        // Verificar quantos pets o cliente já tem
        int petCount = petService.getClientPetCount(clientId);
        if (petCount >= 5) {
            System.out.println("Limite de pets por cliente atingido (máximo: 5).");
            System.out.print("Deseja continuar mesmo assim? (S/N): ");
            if (!scanner.nextLine().equalsIgnoreCase("S")) {
                return;
            }
        }
        
        System.out.print("Nome do pet: ");
        String nome = scanner.nextLine();
        
        System.out.print("Espécie (ex: Cachorro, Gato): ");
        String especie = scanner.nextLine();
        
        System.out.print("Raça: ");
        String raca = scanner.nextLine();
        
        System.out.print("Data de nascimento (dd/mm/aaaa): ");
        String dataNascimento = scanner.nextLine();
        Date birthDate = parseDate(dataNascimento);
        if (birthDate == null) return;
        
        Pet pet = new Pet();
        pet.setClientId(clientId);
        pet.setName(nome);
        pet.setSpecies(especie);
        pet.setBreed(raca);
        pet.setBirthDate(new java.sql.Date(birthDate.getTime()));
        
        int petId = petService.addPet(pet);
        System.out.println("Pet cadastrado com sucesso! ID: " + petId);
        
        // Log de auditoria
        AuditLogService.logAction("Pet cadastrado", "ID: " + petId + ", Nome: " + pet.getName() + ", Dono: " + client.getName());
    }

    private static Date parseDate(String dateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            System.out.println("Formato de data inválido. Use dd/mm/aaaa.");
            return null;
        }
    }

    private static boolean checkDailyAvailability(Date date, AppointmentService appointmentService) throws SQLException {
        List<Appointment> appointments = appointmentService.getAppointmentsByDate(new java.sql.Date(date.getTime()));
        if (appointments.size() >= 10) {
            System.out.println("Limite de consultas para este dia atingido (máximo: 10).");
            return false;
        }
        return true;
    }

    private static List<Availability> getAvailableSlots(Date date, AvailabilityService availabilityService) throws SQLException {
        List<Availability> slots = availabilityService.getAvailabilityByDate(date);
        if (slots.isEmpty()) {
            System.out.println("Gerando horários disponíveis para " + new SimpleDateFormat("dd/MM/yyyy").format(date));
            generateAvailability(date);
            slots = availabilityService.getAvailabilityByDate(date);
        }
        
        if (slots.isEmpty()) {
            System.out.println("Não há horários disponíveis para esta data.");
            return slots;
        }
        
        System.out.println("\nHorários disponíveis:");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        int i = 1;
        for (Availability slot : slots) {
            if (slot.isAvailable()) {
                System.out.println(i++ + ". " + timeFormat.format(slot.getStartTime()));
            }
        }
        
        return slots.stream()
                   .filter(Availability::isAvailable)
                   .toList();
    }

    private static Availability selectTimeSlot(Scanner scanner, List<Availability> availableSlots) {
        System.out.print("\nEscolha um horário (1-" + availableSlots.size() + "): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice < 1 || choice > availableSlots.size()) {
                System.out.println("Opção inválida!");
                return null;
            }
            return availableSlots.get(choice - 1);
        } catch (NumberFormatException e) {
            System.out.println("Por favor, digite um número válido!");
            return null;
        }
    }

    private static Appointment createAppointment(int petId, String service, Availability selectedSlot) {
        Appointment appointment = new Appointment();
        appointment.setPetId(petId);
        appointment.setService(service);
        appointment.setStartTime(new Timestamp(selectedSlot.getStartTime().getTime()));
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(selectedSlot.getStartTime().getTime());
        calendar.add(Calendar.HOUR, 1);
        appointment.setEndTime(new Timestamp(calendar.getTimeInMillis()));
        appointment.setStatus("Agendado");
        return appointment;
    }

    private static Date requestValidDate(Scanner scanner) {
        System.out.print("\nData para o serviço (dd/mm/aaaa): ");
        String dateStr = scanner.nextLine();
        Date date = parseDate(dateStr);
        
        if (date == null) {
            return null;
        }
        
        Calendar cal = Calendar.getInstance();
        Calendar selectedCal = Calendar.getInstance();
        selectedCal.setTime(date);
        
        // Verificar se é uma data futura
        if (date.before(new Date())) {
            System.out.println("Por favor, escolha uma data futura.");
            return null;
        }
        
        // Verificar se é final de semana
        int dayOfWeek = selectedCal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
            System.out.println("Não realizamos atendimentos aos finais de semana.");
            return null;
        }
        
        return date;
    }

    private static void generateAvailability(Date date) throws SQLException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateFormat.format(date);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 9);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        
        AvailabilityService availabilityService = new AvailabilityService();
        
        for (int i = 0; i < 8; i++) {  // 8 slots de 1 hora das 9h às 17h
            Availability slot = new Availability();
            slot.setDate(date);
            slot.setStartTime(new Timestamp(cal.getTimeInMillis()));
            
            cal.add(Calendar.HOUR_OF_DAY, 1);
            slot.setEndTime(new Timestamp(cal.getTimeInMillis()));
            
            slot.setAvailable(true);
            availabilityService.addAvailability(slot);
        }
    }

    private static void showAppointmentConfirmation(Appointment appointment, Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        System.out.println("\nConsulta agendada com sucesso!");
        System.out.println("ID da consulta: " + appointment.getAppointmentId());
        System.out.println("Serviço: " + appointment.getService());
        System.out.println("Data/Hora: " + dateFormat.format(appointment.getStartTime()));
    }

    private static void listarClientes(ClientService clientService) throws SQLException {
        List<Client> clients = clientService.getAllClients();
        
        System.out.println("\nClientes cadastrados:");
        System.out.println("----------------------------------------------------");
        for (Client client : clients) {
            System.out.println("ID: " + client.getClientId());
            System.out.println("Nome: " + client.getName());
            System.out.println("Email: " + client.getEmail());
            System.out.println("Telefone: " + client.getPhone());
            System.out.println("----------------------------------------------------");
        }
    }

    private static void listarPets(PetService petService) throws SQLException {
        List<Pet> pets = petService.getAllPets();
        
        System.out.println("\nPets cadastrados:");
        System.out.println("----------------------------------------------------");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        for (Pet pet : pets) {
            System.out.println("ID: " + pet.getPetId());
            System.out.println("Nome: " + pet.getName());
            System.out.println("ID do Dono: " + pet.getClientId());
            System.out.println("Espécie: " + pet.getSpecies());
            System.out.println("Raça: " + pet.getBreed());
            System.out.println("Data de Nascimento: " + dateFormat.format(pet.getBirthDate()));
            System.out.println("----------------------------------------------------");
        }
    }
}
package vet;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm");
    
    // Constants for validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\([0-9]{2}\\)[0-9]{5}-[0-9]{4}$");

    public static void main(String[] args) {
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
                System.out.println("6. Sair");
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
        System.out.print("Escolha uma opção: ");
        
        int tipoServico = Integer.parseInt(scanner.nextLine());
        String service;
        
        switch (tipoServico) {
            case 1:
                service = ServiceType.BANHO.getDescricao();
                break;
            case 2:
                service = ServiceType.TOSA.getDescricao();
                break;
            case 3:
                service = ServiceType.VACINA.getDescricao();
                if (!petService.checkVaccinesUpToDate(petId)) {
                    System.out.println("ATENÇÃO: Vacinas do pet não estão em dia!");
                    System.out.print("Deseja continuar mesmo assim? (S/N): ");
                    if (!scanner.nextLine().equalsIgnoreCase("S")) {
                        return;
                    }
                }
                break;
            case 4:
                service = ServiceType.CONSULTA.getDescricao();
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
        appointmentService.scheduleAppointment(appointment);
        
        showAppointmentConfirmation(appointment, date);
    }

    private static void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Email inválido!");
        }
    }
    
    private static void validatePhone(String phone) {
        if (!PHONE_PATTERN.matcher(phone).matches()) {
            throw new IllegalArgumentException("Telefone deve estar no formato (99)99999-9999");
        }
    }
    
    private static void validateName(String name) {
        if (name == null || name.trim().length() < 3) {
            throw new IllegalArgumentException("Nome deve ter pelo menos 3 caracteres!");
        }
    }

    private static void cadastrarCliente(Scanner scanner, ClientService clientService) {
        System.out.println("\n=== Cadastro de Cliente ===");
        Client client = new Client();

        while (true) {
            try {
                System.out.print("Nome: ");
                String nome = scanner.nextLine();
                validateName(nome);
                client.setName(nome);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
        
        while (true) {
            try {
                System.out.print("Email: ");
                String email = scanner.nextLine();
                validateEmail(email);
                client.setEmail(email);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
        
        while (true) {
            try {
                System.out.print("Telefone (99)99999-9999: ");
                String phone = scanner.nextLine();
                validatePhone(phone);
                client.setPhone(phone);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Erro: " + e.getMessage());
            }
        }
        
        System.out.print("Endereço: ");
        client.setAddress(scanner.nextLine());

        try {
            clientService.addClient(client);
            System.out.println("Cliente cadastrado com sucesso! ID: " + client.getClientId());
            logger.info("Cliente cadastrado com sucesso: " + client.getName());
        } catch (SQLException e) {
            System.out.println("Erro ao cadastrar cliente: " + e.getMessage());
            logger.severe("Erro ao cadastrar cliente: " + e.getMessage());
        }
    }

    private static void cadastrarPet(Scanner scanner, PetService petService, ClientService clientService) throws SQLException {
        System.out.println("\n=== Cadastro de Pet ===");
        
        System.out.println("\nClientes disponíveis:");
        listarClientes(clientService);
        
        while (true) {
            try {
                System.out.print("\nDigite o ID do Cliente: ");
                int clientId = Integer.parseInt(scanner.nextLine());
                
                Client client = clientService.getClientById(clientId);
                if (client == null) {
                    System.out.println("Cliente não encontrado! Digite um ID válido da lista acima.");
                    continue;
                }

                int petCount = petService.getClientPetCount(clientId);
                if (petCount >= 5) {
                    System.out.println("Cliente já atingiu o limite de 5 pets!");
                    return;
                }

                Pet pet = new Pet();
                pet.setClientId(clientId);

                while (true) {
                    System.out.print("Nome do Pet: ");
                    String nomePet = scanner.nextLine();
                    if (nomePet.length() < 2) {
                        System.out.println("Nome do pet deve ter pelo menos 2 caracteres!");
                        continue;
                    }
                    pet.setName(nomePet);
                    break;
                }

                System.out.print("Espécie: ");
                String species = scanner.nextLine().trim();
                if (species.isEmpty()) {
                    System.out.println("Espécie não pode estar vazia!");
                    continue;
                }
                pet.setSpecies(species);

                System.out.print("Raça: ");
                String breed = scanner.nextLine().trim();
                if (breed.isEmpty()) {
                    System.out.println("Raça não pode estar vazia!");
                    continue;
                }
                pet.setBreed(breed);

                while (true) {
                    try {
                        System.out.print("Data de Nascimento (dd/MM/yyyy): ");
                        String birthDateStr = scanner.nextLine();
                        Date birthDate = parseDate1(birthDateStr);
                        
                        if (birthDate.after(new Date(System.currentTimeMillis()))) {
                            System.out.println("Data de nascimento não pode ser futura!");
                            continue;
                        }
                        
                        pet.setBirthDate(birthDate);
                        break;
                    } catch (IllegalArgumentException e) {
                        System.out.println("Data inválida! Use o formato dd/MM/yyyy");
                    }
                }

                petService.addPet(pet);
                System.out.println("Pet cadastrado com sucesso! ID: " + pet.getPetId());
                logger.info("Pet cadastrado com sucesso: " + pet.getName() + " para o cliente: " + client.getName());
                return;

            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um ID válido!");
            }
        }
    }

    private static Date parseDate1(String dateStr) {
        try {
            DATE_FORMAT.setLenient(false);
            java.util.Date parsed = DATE_FORMAT.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(parsed);
            cal.getTime(); // Validates the date
            return new Date(parsed.getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Data inválida! Use o formato dd/MM/yyyy");
        }
    }

    private static boolean checkDailyAvailability(Date date, AppointmentService appointmentService) throws SQLException {
        List<Appointment> appointments = appointmentService.getAppointmentsByDate(date);
        if (appointments.size() >= 20) {
            System.out.println("Limite de agendamentos para este dia foi atingido (máximo 20)!");
            return false;
        }
        return true;
    }

    private static List<Availability> getAvailableSlots(Date date, AvailabilityService availabilityService) throws SQLException {
        generateAvailability(date);
        
        List<Availability> slots = availabilityService.getAvailableSlots(date);
        if (slots.isEmpty()) {
            System.out.println("Nenhum horário disponível para esta data.");
            return slots;
        }

        System.out.println("\nHorários disponíveis:");
        for (Availability slot : slots) {
            System.out.printf("ID: %d | %s - %s%n",
                    slot.getSlotId(),
                    TIME_FORMAT.format(slot.getStartTime()),
                    TIME_FORMAT.format(slot.getEndTime()));
        }
        
        return slots;
    }

    private static Availability selectTimeSlot(Scanner scanner, List<Availability> availableSlots) {
        while (true) {
            try {
                System.out.print("\nSelecione o ID do horário desejado: ");
                int slotId = Integer.parseInt(scanner.nextLine());
                
                for (Availability slot : availableSlots) {
                    if (slot.getSlotId() == slotId) {
                        return slot;
                    }
                }
                System.out.println("ID de horário inválido! Por favor, escolha um ID da lista.");
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido!");
            }
        }
    }

    private static Appointment createAppointment(int petId, String service, Availability selectedSlot) {
        Appointment appointment = new Appointment();
        appointment.setPetId(petId);
        appointment.setService(service);
        appointment.setServiceType(ServiceType.valueOf(service.toUpperCase()));
        appointment.setStartTime(selectedSlot.getStartTime());
        appointment.setEndTime(selectedSlot.getEndTime());
        appointment.setStatus("agendado");
        appointment.setNotes("Agendamento realizado em " + 
                DATE_FORMAT.format(new java.util.Date()) + " " + 
                TIME_FORMAT.format(new java.util.Date()));
        return appointment;
    }

    private static Date requestValidDate(Scanner scanner) {
        while (true) {
            try {
                System.out.print("Data (dd/MM/yyyy): ");
                String dateStr = scanner.nextLine();
                Date date = parseDate1(dateStr);
                
                LocalDate hoje = LocalDate.now();
                LocalDate dataAgendamento = date.toLocalDate();
                
                if (dataAgendamento.isBefore(hoje)) {
                    System.out.println("Data não pode ser anterior a hoje!");
                    continue;
                }
                
                if (dataAgendamento.isAfter(hoje.plusDays(60))) {
                    System.out.println("Erro: Agendamentos só podem ser feitos para até 60 dias no futuro!");
                    continue;
                }
                
                return date;
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void generateAvailability(Date date) throws SQLException {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 8);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        
        Calendar endCal = Calendar.getInstance();
        endCal.setTime(date);
        endCal.set(Calendar.HOUR_OF_DAY, 18);
        endCal.set(Calendar.MINUTE, 0);
        endCal.set(Calendar.SECOND, 0);
        
        AvailabilityService availabilityService = new AvailabilityService();
        
        while (cal.before(endCal)) {
            Availability slot = new Availability();
            slot.setDate(date);
            slot.setStartTime(new Timestamp(cal.getTimeInMillis()));
            
            cal.add(Calendar.MINUTE, 30);
            slot.setEndTime(new Timestamp(cal.getTimeInMillis()));
            slot.setAvailable(true);
            
            availabilityService.addSlot(slot);
        }
    }

    private static void showAppointmentConfirmation(Appointment appointment, Date date) {
        System.out.println("\nAgendamento realizado com sucesso!");
        System.out.println("Serviço: " + appointment.getService());
        System.out.println("Data: " + DATE_FORMAT.format(date));
        System.out.println("Horário: " + TIME_FORMAT.format(appointment.getStartTime()) + 
                          " - " + TIME_FORMAT.format(appointment.getEndTime()));
        System.out.println("Status: " + appointment.getStatus());
    }

    private static void listarClientes(ClientService clientService) throws SQLException {
        System.out.println("\n=== Lista de Clientes ===");
        List<Client> clients = clientService.getAllClients();
        if (clients.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }
        for (Client client : clients) {
            System.out.println("ID: " + client.getClientId() + 
                             " | Nome: " + client.getName() + 
                             " | Email: " + client.getEmail() + 
                             " | Telefone: " + client.getPhone());
        }
    }

    private static void listarPets(PetService petService) throws SQLException {
        System.out.println("\n=== Lista de Pets ===");
        List<Pet> pets = petService.getAllPets();
        if (pets.isEmpty()) {
            System.out.println("Nenhum pet cadastrado.");
            return;
        }
        for (Pet pet : pets) {
            System.out.println("ID: " + pet.getPetId() + 
                             " | Nome: " + pet.getName() + 
                             " | Espécie: " + pet.getSpecies() + 
                             " | Raça: " + pet.getBreed() + 
                             " | Cliente ID: " + pet.getClientId());
        }
    }
}
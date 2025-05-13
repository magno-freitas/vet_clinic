

import ui.*;
import util.LoggerUtil;
import java.sql.SQLException;
import java.util.Scanner;

public class MainFrame {
    private final Scanner scanner;
    private final ClientService clientService;
    private final PetService petService;
    private final AppointmentService appointmentService;
    private final AvailabilityService availabilityService;

    public MainFrame() {
        this.scanner = new Scanner(System.in);
        this.clientService = new ClientService();
        this.petService = new PetService();
        this.appointmentService = new AppointmentService();
        this.availabilityService = new AvailabilityService();
    }

    public void start() {
        while (true) {
            try {
                displayMenu();
                int option = Integer.parseInt(scanner.nextLine());
                
                if (handleMenuOption(option)) {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite um número válido!");
                LoggerUtil.logWarning("Invalid number format entered by user");
            } catch (SQLException e) {
                System.out.println("Erro de banco de dados: " + e.getMessage());
                LoggerUtil.logError("Database error", e);
            }
        }
    }

    private void displayMenu() {
        System.out.println("\n=== Sistema Veterinário ===");
        System.out.println("1. Cadastrar Cliente");
        System.out.println("2. Cadastrar Pet");
        System.out.println("3. Agendar Consulta");
        System.out.println("4. Listar Clientes");
        System.out.println("5. Listar Pets");
        System.out.println("6. Verificar Status de Consulta");
        System.out.println("7. Sair");
        System.out.print("Escolha uma opção: ");
    }

    private boolean handleMenuOption(int option) throws SQLException {
        switch (option) {
            case 1:
                new ClientRegistrationUI(scanner, clientService).register();
                break;
            case 2:
                new PetRegistrationUI(scanner, petService, clientService).register();
                break;
            case 3:
                new AppointmentUI(scanner, petService, appointmentService, availabilityService).schedule();
                break;
            case 4:
                new ClientListUI(clientService).display();
                break;
            case 5:
                new PetListUI(petService).display();
                break;
            case 6:
                new AppointmentStatusUI(scanner, clientService, appointmentService).check();
                break;
            case 7:
                System.out.println("Encerrando o programa...");
                scanner.close();
                return true;
            default:
                System.out.println("Opção inválida!");
        }
        return false;
    }
}
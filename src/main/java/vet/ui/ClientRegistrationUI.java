package vet.ui;

import vet.model.Client;
import vet.service.ClientService;
import vet.util.ValidationUtil;
import vet.exception.VetClinicException;
import java.sql.SQLException;
import java.util.Scanner;

public class ClientRegistrationUI {
    private final Scanner scanner;
    private final ClientService clientService;

    public ClientRegistrationUI(Scanner scanner, ClientService clientService) {
        this.scanner = scanner;
        this.clientService = clientService;
    }

    public void register() throws SQLException {
        System.out.println("\n=== Cadastro de Cliente ===");
        
        try {
            String name = requestName();
            String email = requestEmail();
            String phone = requestPhone();
            String address = requestAddress();

            Client client = new Client();
            client.setName(name);
            client.setEmail(email);
            client.setPhone(phone);
            client.setAddress(address);

            clientService.addClient(client);
            System.out.println("Cliente cadastrado com sucesso!");
            
        } catch (VetClinicException e) {
            System.out.println("Erro no cadastro: " + e.getMessage());
        }
    }

    private String requestName() {
        System.out.print("Nome: ");
        String name = scanner.nextLine();
        ValidationUtil.validateNotEmpty(name, "Nome");
        return name;
    }

    private String requestEmail() {
        System.out.print("Email: ");
        String email = scanner.nextLine();
        ValidationUtil.validateEmail(email);
        return email;
    }

    private String requestPhone() {
        System.out.print("Telefone: ");
        String phone = scanner.nextLine();
        ValidationUtil.validatePhone(phone);
        return phone;
    }

    private String requestAddress() {
        System.out.print("Endereço: ");
        String address = scanner.nextLine();
        ValidationUtil.validateNotEmpty(address, "Endereço");
        return address;
    }
}
package vet.ui;

import vet.*;
import vet.util.ValidationUtil;
import vet.util.LoggerUtil;
import vet.exception.VetClinicException;
import java.sql.SQLException;
import java.util.Scanner;

public class PetRegistrationUI {
    private final Scanner scanner;
    private final PetService petService;
    private final ClientService clientService;

    public PetRegistrationUI(Scanner scanner, PetService petService, ClientService clientService) {
        this.scanner = scanner;
        this.petService = petService;
        this.clientService = clientService;
    }

    public void register() throws SQLException {
        System.out.println("\n=== Cadastro de Pet ===");
        
        try {
            // Get client ID
            System.out.print("Email do Proprietário: ");
            String email = scanner.nextLine();
            System.out.print("Telefone do Proprietário: ");
            String phone = scanner.nextLine();
            
            int clientId = clientService.getClientId(email, phone);
            if (clientId == -1) {
                throw new VetClinicException("Cliente não encontrado");
            }
            
            // Get pet details
            String name = requestName();
            String species = requestSpecies();
            int age = requestAge();
            
            Pet pet = new Pet();
            pet.setClientId(clientId);
            pet.setName(name);
            pet.setSpecies(species);
            pet.setAge(age);
            
            petService.addPet(pet);
            System.out.println("Pet cadastrado com sucesso!");
            
        } catch (NumberFormatException e) {
            throw new VetClinicException("Invalid number format", e);
        } catch (VetClinicException e) {
            System.out.println("Erro no cadastro: " + e.getMessage());
            LoggerUtil.logError("Pet registration error", e);
        }
    }

    private String requestName() {
        System.out.print("Nome do Pet: ");
        String name = scanner.nextLine();
        ValidationUtil.validateNotEmpty(name, "Nome do Pet");
        return name;
    }

    private String requestSpecies() {
        System.out.print("Espécie: ");
        String species = scanner.nextLine();
        ValidationUtil.validateNotEmpty(species, "Espécie");
        return species;
    }

    private int requestAge() {
        System.out.print("Idade: ");
        String ageStr = scanner.nextLine();
        try {
            int age = Integer.parseInt(ageStr);
            if (age < 0 || age > 30) {
                throw new VetClinicException("Idade inválida (deve estar entre 0 e 30 anos)");
            }
            return age;
        } catch (NumberFormatException e) {
            throw new VetClinicException("Idade deve ser um número", e);
        }
    }
}
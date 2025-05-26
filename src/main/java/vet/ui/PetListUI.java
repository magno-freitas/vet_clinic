package ui;

import vet.ui.*;
import vet.util.LoggerUtil;
import java.sql.SQLException;
import java.util.List;

import vet.model.Pet;
import vet.service.PetService;

public class PetListUI {
    private final PetService petService;

    public PetListUI(PetService petService) {
        this.petService = petService;
    }

    public void display() throws SQLException {
        try {
            List<Pet> pets = petService.getAllPets();
            if (pets.isEmpty()) {
                System.out.println("Não há pets cadastrados.");
                return;
            }

            System.out.println("\n=== Lista de Pets ===");
            for (Pet pet : pets) {
                System.out.printf("ID: %d | Nome: %s | Espécie: %s | Idade: %d%n",
                    pet.getId(),
                    pet.getName(),
                    pet.getSpecies(),
                    pet.getAge());
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Error listing pets", e);
            throw e;
        }
    }
}
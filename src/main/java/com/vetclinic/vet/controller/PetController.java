package com.vetclinic.vet.controller;

import com.vetclinic.vet.model.Pet;
import com.vetclinic.vet.service.PetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pets")
public class PetController {

    private final PetService petService;

    @Autowired
    public PetController(PetService petService) {
        this.petService = petService;
    }

    @PostMapping("/clients/{clientId}")
    public ResponseEntity<Pet> createPet(
            @PathVariable Integer clientId,
            @Valid @RequestBody Pet pet) {
        Pet savedPet = petService.createPet(pet, clientId);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedPet.getId())
                .toUri();
        return ResponseEntity.created(location).body(savedPet);
    }

    @GetMapping
    public ResponseEntity<List<Pet>> getAllPets() {
        return ResponseEntity.ok(petService.getAllPets());
    }

    @GetMapping("/clients/{clientId}")
    public ResponseEntity<List<Pet>> getPetsByClientId(@PathVariable Integer clientId) {
        return ResponseEntity.ok(petService.getPetsByClientId(clientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getPetById(@PathVariable Integer id) {
        return petService.getPetById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pet> updatePet(
            @PathVariable Integer id,
            @Valid @RequestBody Pet petDetails) {
        Pet updatedPet = petService.updatePet(id, petDetails);
        return ResponseEntity.ok(updatedPet);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePet(@PathVariable Integer id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }
}

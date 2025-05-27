package com.veterinary.vetclinic.service;

import com.veterinary.vetclinic.model.Pet;
import com.veterinary.vetclinic.repository.PetRepository;
import com.veterinary.vetclinic.service.impl.PetServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    private PetService petService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        petService = new PetServiceImpl(petRepository);
    }

    @Test
    void findAll_ShouldReturnAllPets() {
        // Arrange
        Pet pet1 = new Pet();
        pet1.setPetId(1);
        pet1.setName("Max");
        
        Pet pet2 = new Pet();
        pet2.setPetId(2);
        pet2.setName("Bella");
        
        when(petRepository.findAll()).thenReturn(Arrays.asList(pet1, pet2));

        // Act
        List<Pet> pets = petService.findAll();

        // Assert
        assertEquals(2, pets.size());
        verify(petRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenPetExists_ShouldReturnPet() {
        // Arrange
        Pet pet = new Pet();
        pet.setPetId(1);
        pet.setName("Max");
        
        when(petRepository.findById(1)).thenReturn(Optional.of(pet));

        // Act
        Optional<Pet> result = petService.findById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Max", result.get().getName());
        verify(petRepository, times(1)).findById(1);
    }

    @Test
    void save_ShouldReturnSavedPet() {
        // Arrange
        Pet pet = new Pet();
        pet.setName("Max");
        pet.setSpecies("Dog");
        
        when(petRepository.save(any(Pet.class))).thenReturn(pet);

        // Act
        Pet savedPet = petService.save(pet);

        // Assert
        assertNotNull(savedPet);
        assertEquals("Max", savedPet.getName());
        verify(petRepository, times(1)).save(pet);
    }

    @Test
    void deleteById_ShouldDeletePet() {
        // Arrange
        doNothing().when(petRepository).deleteById(1);

        // Act
        petService.deleteById(1);

        // Assert
        verify(petRepository, times(1)).deleteById(1);
    }
}
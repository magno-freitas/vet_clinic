package com.veterinary.vetclinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.veterinary.vetclinic.model.Pet;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {
    // Custom query methods can be added here
}
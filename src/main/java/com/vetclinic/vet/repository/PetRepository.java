package com.vetclinic.vet.repository;

import com.vetclinic.vet.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Integer> {
    List<Pet> findByClientId(Integer clientId);
    List<Pet> findBySpecies(String species);
    boolean existsByClientIdAndName(Integer clientId, String name);
}

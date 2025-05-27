package com.veterinary.vetclinic.service;

import com.veterinary.vetclinic.model.Pet;
import java.util.List;
import java.util.Optional;

public interface PetService {
    List<Pet> findAll();
    Optional<Pet> findById(Integer id);
    Pet save(Pet pet);
    void deleteById(Integer id);
}
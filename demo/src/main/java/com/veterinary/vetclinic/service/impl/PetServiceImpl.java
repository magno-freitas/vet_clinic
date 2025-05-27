package com.veterinary.vetclinic.service.impl;

import com.veterinary.vetclinic.model.Pet;
import com.veterinary.vetclinic.repository.PetRepository;
import com.veterinary.vetclinic.service.PetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PetServiceImpl implements PetService {
    
    private final PetRepository petRepository;

    @Autowired
    public PetServiceImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public List<Pet> findAll() {
        return petRepository.findAll();
    }

    @Override
    public Optional<Pet> findById(Integer id) {
        return petRepository.findById(id);
    }

    @Override
    public Pet save(Pet pet) {
        return petRepository.save(pet);
    }

    @Override
    public void deleteById(Integer id) {
        petRepository.deleteById(id);
    }
}
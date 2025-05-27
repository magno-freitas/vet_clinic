package com.veterinary.vetclinic.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.veterinary.vetclinic.model.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    // Custom query methods can be added here
}
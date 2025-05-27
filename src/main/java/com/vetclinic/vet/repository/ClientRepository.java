package com.vetclinic.vet.repository;

import com.vetclinic.vet.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByEmail(String email);
    Optional<Client> findByPhone(String phone);
    boolean existsByEmail(String email);
    vet.model.Client save(vet.model.Client client);
}

package com.veterinary.vetclinic.service;

import com.veterinary.vetclinic.model.Client;
import java.util.List;
import java.util.Optional;

public interface ClientService {
    List<Client> findAll();
    Optional<Client> findById(Integer id);
    Client save(Client client);
    void deleteById(Integer id);
}
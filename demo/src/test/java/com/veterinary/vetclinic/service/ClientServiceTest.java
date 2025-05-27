package com.veterinary.vetclinic.service;

import com.veterinary.vetclinic.model.Client;
import com.veterinary.vetclinic.repository.ClientRepository;
import com.veterinary.vetclinic.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    private ClientService clientService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        clientService = new ClientServiceImpl(clientRepository);
    }

    @Test
    void findAll_ShouldReturnAllClients() {
        // Arrange
        Client client1 = new Client("John Doe", "john@example.com", "1234567890", "123 Main St");
        Client client2 = new Client("Jane Doe", "jane@example.com", "0987654321", "456 Oak St");
        
        when(clientRepository.findAll()).thenReturn(Arrays.asList(client1, client2));

        // Act
        List<Client> clients = clientService.findAll();

        // Assert
        assertEquals(2, clients.size());
        verify(clientRepository, times(1)).findAll();
    }

    @Test
    void findById_WhenClientExists_ShouldReturnClient() {
        // Arrange
        Client client = new Client("John Doe", "john@example.com", "1234567890", "123 Main St");
        client.setClientId(1);
        
        when(clientRepository.findById(1)).thenReturn(Optional.of(client));

        // Act
        Optional<Client> result = clientService.findById(1);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("John Doe", result.get().getName());
        verify(clientRepository, times(1)).findById(1);
    }

    @Test
    void save_ShouldReturnSavedClient() {
        // Arrange
        Client client = new Client("John Doe", "john@example.com", "1234567890", "123 Main St");
        
        when(clientRepository.save(any(Client.class))).thenReturn(client);

        // Act
        Client savedClient = clientService.save(client);

        // Assert
        assertNotNull(savedClient);
        assertEquals("John Doe", savedClient.getName());
        verify(clientRepository, times(1)).save(client);
    }

    @Test
    void deleteById_ShouldDeleteClient() {
        // Arrange
        doNothing().when(clientRepository).deleteById(1);

        // Act
        clientService.deleteById(1);

        // Assert
        verify(clientRepository, times(1)).deleteById(1);
    }
}
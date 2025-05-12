package ui;

import ui.*;
import util.LoggerUtil;
import java.sql.SQLException;
import java.util.List;

import com.mysql.cj.xdevapi.Client;

public class ClientListUI<ClientService> {
    private final ClientService clientService;

    public ClientListUI(ClientService clientService) {
        this.clientService = clientService;
    }

    public void display() throws SQLException {
        try {
            List<Client> clients = clientService.getAllClients();
            if (clients.isEmpty()) {
                System.out.println("Não há clientes cadastrados.");
                return;
            }

            System.out.println("\n=== Lista de Clientes ===");
            for (Client client : clients) {
                System.out.printf("ID: %d | Nome: %s | Email: %s | Telefone: %s%n",
                    client.getId(),
                    client.getName(),
                    client.getEmail(),
                    client.getPhone());
            }
        } catch (SQLException e) {
            LoggerUtil.logError("Error listing clients", e);
            throw e;
        }
    }
}
package br.com.jhonnyazevedo.timegrid_backend.client.service;

import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;

import java.util.List;
import java.util.UUID;

public interface ClientService {

    Client createClient(UUID userId, Client client);

    List<Client> listByUser(UUID userId);

    Client findById(UUID id);

    Client updateClient(UUID id, Client client);

    void deleteClient(UUID id);
}
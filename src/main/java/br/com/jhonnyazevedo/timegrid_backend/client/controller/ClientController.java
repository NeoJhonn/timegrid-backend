package br.com.jhonnyazevedo.timegrid_backend.client.controller;

import br.com.jhonnyazevedo.timegrid_backend.client.dto.ClientRequest;
import br.com.jhonnyazevedo.timegrid_backend.client.dto.ClientResponse;
import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;
import br.com.jhonnyazevedo.timegrid_backend.client.mapper.ClientMapper;
import br.com.jhonnyazevedo.timegrid_backend.client.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @PostMapping("/users/{userId}/clients")
    public ResponseEntity<ClientResponse> createClient(
            @PathVariable UUID userId,
            @RequestBody @Valid ClientRequest request
    ) {
        Client client = clientMapper.toEntity(request);
        Client createdClient = clientService.createClient(userId, client);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientMapper.toResponse(createdClient));
    }

    @GetMapping("/users/{userId}/clients")
    public ResponseEntity<List<ClientResponse>> listByUser(@PathVariable UUID userId) {
        List<Client> clients = clientService.listByUser(userId);
        return ResponseEntity.ok(clientMapper.toResponseList(clients));
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<ClientResponse> findById(@PathVariable UUID id) {
        Client client = clientService.findById(id);
        return ResponseEntity.ok(clientMapper.toResponse(client));
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable UUID id,
            @RequestBody @Valid ClientRequest request
    ) {
        Client client = clientMapper.toEntity(request);
        Client updatedClient = clientService.updateClient(id, client);
        return ResponseEntity.ok(clientMapper.toResponse(updatedClient));
    }

    @DeleteMapping("/users/{userId}/clients/{clientId}")
    public ResponseEntity<Void> deleteClient(
            @PathVariable UUID userId,
            @PathVariable UUID clientId
    ) {
        clientService.deleteClient(userId, clientId);
        return ResponseEntity.noContent().build();
    }
}

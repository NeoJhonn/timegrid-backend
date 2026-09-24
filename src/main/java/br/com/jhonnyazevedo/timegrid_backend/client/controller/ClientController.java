package br.com.jhonnyazevedo.timegrid_backend.client.controller;

import br.com.jhonnyazevedo.timegrid_backend.client.dto.ClientRequest;
import br.com.jhonnyazevedo.timegrid_backend.client.dto.ClientResponse;
import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;
import br.com.jhonnyazevedo.timegrid_backend.client.mapper.ClientMapper;
import br.com.jhonnyazevedo.timegrid_backend.client.service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Clients", description = "Endpoints para gerenciamento de clientes por usuario")
public class ClientController {

    private final ClientService clientService;
    private final ClientMapper clientMapper;

    @PostMapping("/users/{userId}/clients")
    @Operation(summary = "Cria cliente", description = "Cria um cliente vinculado ao usuario informado no path.")
    public ResponseEntity<ClientResponse> createClient(
            @PathVariable UUID userId,
            @RequestBody @Valid ClientRequest request
    ) {
        Client client = clientMapper.toEntity(request);
        Client createdClient = clientService.createClient(userId, client);
        return ResponseEntity.status(HttpStatus.CREATED).body(clientMapper.toResponse(createdClient));
    }

    @GetMapping("/users/{userId}/clients")
    @Operation(summary = "Lista clientes do usuario", description = "Retorna todos os clientes vinculados ao usuario informado.")
    public ResponseEntity<List<ClientResponse>> listByUser(@PathVariable UUID userId) {
        List<Client> clients = clientService.listByUser(userId);
        return ResponseEntity.ok(clientMapper.toResponseList(clients));
    }

    @GetMapping("/users/{userId}/clients/{clientId}")
    @Operation(summary = "Busca cliente por ID", description = "Busca um cliente e valida se ele pertence ao usuario informado.")
    public ResponseEntity<ClientResponse> findById(
            @PathVariable UUID userId,
            @PathVariable UUID clientId
    ) {
        Client client = clientService.findById(userId, clientId);
        return ResponseEntity.ok(clientMapper.toResponse(client));
    }

    @PutMapping("/users/{userId}/clients/{clientId}")
    @Operation(summary = "Atualiza cliente", description = "Atualiza nome e telefone de um cliente pertencente ao usuario informado.")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable UUID userId,
            @PathVariable UUID clientId,
            @RequestBody @Valid ClientRequest request
    ) {
        Client client = clientMapper.toEntity(request);
        Client updatedClient = clientService.updateClient(userId, clientId, client);
        return ResponseEntity.ok(clientMapper.toResponse(updatedClient));
    }

    @DeleteMapping("/users/{userId}/clients/{clientId}")
    @Operation(summary = "Remove cliente", description = "Remove um cliente apos validar que ele pertence ao usuario informado.")
    public ResponseEntity<Void> deleteClient(
            @PathVariable UUID userId,
            @PathVariable UUID clientId
    ) {
        clientService.deleteClient(userId, clientId);
        return ResponseEntity.noContent().build();
    }
}

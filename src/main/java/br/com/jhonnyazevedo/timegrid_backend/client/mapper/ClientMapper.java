package br.com.jhonnyazevedo.timegrid_backend.client.mapper;

import br.com.jhonnyazevedo.timegrid_backend.client.dto.ClientRequest;
import br.com.jhonnyazevedo.timegrid_backend.client.dto.ClientResponse;
import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class ClientMapper {

    public Client toEntity(ClientRequest request) {
        Client client = new Client();
        client.setName(request.name());
        client.setPhone(request.phone());
        return client;
    }

    public ClientResponse toResponse(Client client) {
        UUID userId = client.getUser() != null ? client.getUser().getId() : null;

        return new ClientResponse(
                client.getId(),
                client.getName(),
                client.getPhone(),
                userId,
                client.getCreatedAt()
        );
    }

    public List<ClientResponse> toResponseList(List<Client> clients) {
        return clients.stream()
                .map(this::toResponse)
                .toList();
    }
}
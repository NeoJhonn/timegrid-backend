package br.com.jhonnyazevedo.timegrid_backend.client.service;

import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;
import br.com.jhonnyazevedo.timegrid_backend.client.repository.ClientRepository;
import br.com.jhonnyazevedo.timegrid_backend.exception.BusinessException;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {

    private final ClientRepository clientRepository;
    private final UserRepository userRepository;

    @Override
    public Client createClient(UUID userId, Client client) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));

        if (clientRepository.existsByUserAndPhone(user, client.getPhone())) {
            throw new BusinessException("Cliente já cadastrado com esse telefone.");
        }

        client.setUser(user);

        return clientRepository.save(client);
    }

    @Override
    public List<Client> listByUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado."));

        return clientRepository.findByUser(user);
    }

    @Override
    public Client findById(UUID userId, UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado."));

        if (client.getUser() == null || !client.getUser().getId().equals(userId)) {
            throw new BusinessException("Cliente não pertence ao usuário");
        }

        return client;
    }

    @Override
    public Client updateClient(UUID userId, UUID clientId, Client client) {

        Client existing = findById(userId, clientId);

        if (client.getName() == null || client.getName().isBlank()) {
            throw new BusinessException("Nome do cliente é obrigatório.");
        }

        if (client.getPhone() == null || client.getPhone().isBlank()) {
            throw new BusinessException("Telefone do cliente é obrigatório.");
        }

        clientRepository.findByUserAndPhone(existing.getUser(), client.getPhone())
                .filter(found -> !found.getId().equals(clientId))
                .ifPresent(found -> {
                    throw new BusinessException("Cliente já cadastrado com esse telefone.");
                });

        existing.setName(client.getName());
        existing.setPhone(client.getPhone());

        return clientRepository.save(existing);
    }

    @Override
    public void deleteClient(UUID userId, UUID clientId) {
        Client client = findById(userId, clientId);
        clientRepository.delete(client);
    }
}

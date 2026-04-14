package br.com.jhonnyazevedo.timegrid_backend.client.service;

import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;
import br.com.jhonnyazevedo.timegrid_backend.client.repository.ClientRepository;
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
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        if (clientRepository.existsByUserAndPhone(user, client.getPhone())) {
            throw new RuntimeException("Cliente já cadastrado com esse telefone.");
        }

        client.setUser(user);

        return clientRepository.save(client);
    }

    @Override
    public List<Client> listByUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        return clientRepository.findByUser(user);
    }

    @Override
    public Client findById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));
    }

    @Override
    public Client updateClient(UUID id, Client client) {

        Client existing = findById(id);

        existing.setName(client.getName());
        existing.setPhone(client.getPhone());

        return clientRepository.save(existing);
    }

    @Override
    public void deleteClient(UUID id) {
        clientRepository.deleteById(id);
    }
}

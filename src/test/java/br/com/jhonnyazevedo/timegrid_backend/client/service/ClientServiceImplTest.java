package br.com.jhonnyazevedo.timegrid_backend.client.service;

import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;
import br.com.jhonnyazevedo.timegrid_backend.client.repository.ClientRepository;
import br.com.jhonnyazevedo.timegrid_backend.exception.BusinessException;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private UUID userId;
    private UUID clientId;
    private User user;
    private Client client;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        clientId = UUID.randomUUID();

        user = new User();
        user.setId(userId);

        client = new Client();
        client.setId(clientId);
        client.setName("Carlos Silva");
        client.setPhone("11999990001");
        client.setUser(user);
    }

    @Test
    void createClient_shouldSaveClientWhenDataIsValid() {
        Client request = createClientRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(clientRepository.existsByUserAndPhone(user, request.getPhone())).thenReturn(false);
        when(clientRepository.save(request)).thenReturn(request);

        Client savedClient = clientService.createClient(userId, request);

        assertSame(user, savedClient.getUser());
        verify(clientRepository).save(request);
    }

    @Test
    void createClient_shouldThrowBusinessExceptionWhenUserDoesNotExist() {
        Client request = createClientRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> clientService.createClient(userId, request)
        );

        verify(clientRepository, never()).save(request);
    }

    @Test
    void createClient_shouldThrowBusinessExceptionWhenPhoneAlreadyExistsForUser() {
        Client request = createClientRequest();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(clientRepository.existsByUserAndPhone(user, request.getPhone())).thenReturn(true);

        assertThrows(
                BusinessException.class,
                () -> clientService.createClient(userId, request)
        );

        verify(clientRepository, never()).save(request);
    }

    @Test
    void listByUser_shouldReturnClientsFromUser() {
        List<Client> clients = List.of(client);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(clientRepository.findByUser(user)).thenReturn(clients);

        List<Client> result = clientService.listByUser(userId);

        assertEquals(clients, result);
        verify(clientRepository).findByUser(user);
    }

    @Test
    void findById_shouldReturnClientWhenClientBelongsToUser() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        Client foundClient = clientService.findById(userId, clientId);

        assertSame(client, foundClient);
    }

    @Test
    void findById_shouldThrowBusinessExceptionWhenClientDoesNotBelongToUser() {
        User anotherUser = new User();
        anotherUser.setId(UUID.randomUUID());
        client.setUser(anotherUser);

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        assertThrows(
                BusinessException.class,
                () -> clientService.findById(userId, clientId)
        );
    }

    @Test
    void updateClient_shouldUpdateNameAndPhoneWhenDataIsValid() {
        Client request = createClientRequest();
        request.setName("Marina Souza");
        request.setPhone("11999990002");

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.findByUserAndPhone(user, request.getPhone())).thenReturn(Optional.empty());
        when(clientRepository.save(client)).thenReturn(client);

        Client updatedClient = clientService.updateClient(userId, clientId, request);

        assertEquals("Marina Souza", updatedClient.getName());
        assertEquals("11999990002", updatedClient.getPhone());
        assertSame(user, updatedClient.getUser());
        verify(clientRepository).save(client);
    }

    @Test
    void updateClient_shouldThrowBusinessExceptionWhenPhoneAlreadyExistsForAnotherClient() {
        Client request = createClientRequest();
        request.setPhone("11999990002");

        Client anotherClient = createClientRequest();
        anotherClient.setId(UUID.randomUUID());
        anotherClient.setUser(user);
        anotherClient.setPhone(request.getPhone());

        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.findByUserAndPhone(user, request.getPhone())).thenReturn(Optional.of(anotherClient));

        assertThrows(
                BusinessException.class,
                () -> clientService.updateClient(userId, clientId, request)
        );

        verify(clientRepository, never()).save(client);
    }

    @Test
    void deleteClient_shouldDeleteClientWhenClientBelongsToUser() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

        clientService.deleteClient(userId, clientId);

        verify(clientRepository).delete(client);
    }

    private Client createClientRequest() {
        Client request = new Client();
        request.setName("Carlos Silva");
        request.setPhone("11999990001");
        return request;
    }
}

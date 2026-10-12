package br.com.jhonnyazevedo.timegrid_backend.client.controller;

import br.com.jhonnyazevedo.timegrid_backend.client.dto.ClientResponse;
import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;
import br.com.jhonnyazevedo.timegrid_backend.client.mapper.ClientMapper;
import br.com.jhonnyazevedo.timegrid_backend.client.service.ClientService;
import br.com.jhonnyazevedo.timegrid_backend.exception.BusinessException;
import br.com.jhonnyazevedo.timegrid_backend.exception.GlobalExceptionHandler;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ClientControllerTest {

    @Mock
    private ClientService clientService;

    @Mock
    private ClientMapper clientMapper;

    private MockMvc mockMvc;
    private UUID userId;
    private UUID clientId;
    private Client client;
    private ClientResponse clientResponse;

    @BeforeEach
    void setUp() {
        ClientController clientController = new ClientController(clientService, clientMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(clientController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userId = UUID.randomUUID();
        clientId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        client = new Client();
        client.setId(clientId);
        client.setName("Carlos Silva");
        client.setPhone("11999990001");
        client.setUser(user);

        clientResponse = new ClientResponse(
                clientId,
                "Carlos Silva",
                "11999990001",
                userId,
                null
        );
    }

    @Test
    void createClient_shouldReturnCreatedClient() throws Exception {
        String requestBody = """
                {
                  "name": "Carlos Silva",
                  "phone": "11999990001"
                }
                """;

        when(clientMapper.toEntity(any())).thenReturn(client);
        when(clientService.createClient(userId, client)).thenReturn(client);
        when(clientMapper.toResponse(client)).thenReturn(clientResponse);

        mockMvc.perform(post("/users/{userId}/clients", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(clientId.toString()))
                .andExpect(jsonPath("$.name").value("Carlos Silva"))
                .andExpect(jsonPath("$.userId").value(userId.toString()));
    }

    @Test
    void createClient_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                  "name": "",
                  "phone": ""
                }
                """;

        mockMvc.perform(post("/users/{userId}/clients", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.name").exists())
                .andExpect(jsonPath("$.fields.phone").exists());

        verify(clientService, never()).createClient(any(), any());
    }

    @Test
    void listByUser_shouldReturnClients() throws Exception {
        when(clientService.listByUser(userId)).thenReturn(List.of(client));
        when(clientMapper.toResponseList(List.of(client))).thenReturn(List.of(clientResponse));

        mockMvc.perform(get("/users/{userId}/clients", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(clientId.toString()))
                .andExpect(jsonPath("$[0].phone").value("11999990001"));
    }

    @Test
    void findById_shouldReturnClientWhenClientExists() throws Exception {
        when(clientService.findById(userId, clientId)).thenReturn(client);
        when(clientMapper.toResponse(client)).thenReturn(clientResponse);

        mockMvc.perform(get("/users/{userId}/clients/{clientId}", userId, clientId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(clientId.toString()))
                .andExpect(jsonPath("$.name").value("Carlos Silva"));
    }

    @Test
    void findById_shouldReturnBadRequestWhenBusinessExceptionIsThrown() throws Exception {
        when(clientService.findById(userId, clientId))
                .thenThrow(new BusinessException("Cliente nao pertence ao usuario"));

        mockMvc.perform(get("/users/{userId}/clients/{clientId}", userId, clientId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Cliente nao pertence ao usuario"));
    }

    @Test
    void updateClient_shouldReturnUpdatedClient() throws Exception {
        String requestBody = """
                {
                  "name": "Marina Souza",
                  "phone": "11999990002"
                }
                """;

        Client updatedClient = new Client();
        updatedClient.setId(clientId);
        updatedClient.setName("Marina Souza");
        updatedClient.setPhone("11999990002");

        ClientResponse updatedResponse = new ClientResponse(
                clientId,
                "Marina Souza",
                "11999990002",
                userId,
                null
        );

        when(clientMapper.toEntity(any())).thenReturn(updatedClient);
        when(clientService.updateClient(eq(userId), eq(clientId), eq(updatedClient))).thenReturn(updatedClient);
        when(clientMapper.toResponse(updatedClient)).thenReturn(updatedResponse);

        mockMvc.perform(put("/users/{userId}/clients/{clientId}", userId, clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Marina Souza"))
                .andExpect(jsonPath("$.phone").value("11999990002"));
    }

    @Test
    void deleteClient_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/{userId}/clients/{clientId}", userId, clientId))
                .andExpect(status().isNoContent());

        verify(clientService).deleteClient(userId, clientId);
    }
}

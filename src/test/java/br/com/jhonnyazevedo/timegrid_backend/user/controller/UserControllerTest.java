package br.com.jhonnyazevedo.timegrid_backend.user.controller;

import br.com.jhonnyazevedo.timegrid_backend.enums.UserRole;
import br.com.jhonnyazevedo.timegrid_backend.exception.BusinessException;
import br.com.jhonnyazevedo.timegrid_backend.exception.GlobalExceptionHandler;
import br.com.jhonnyazevedo.timegrid_backend.user.dto.UserRequest;
import br.com.jhonnyazevedo.timegrid_backend.user.dto.UserResponse;
import br.com.jhonnyazevedo.timegrid_backend.user.dto.UserUpdateRequest;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.user.mapper.UserMapper;
import br.com.jhonnyazevedo.timegrid_backend.user.service.UserService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    private MockMvc mockMvc;
    private UUID userId;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        UserController userController = new UserController(userService, userMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
        user.setUsername("john_manager");
        user.setEmail("john.manager@timegrid.test");
        user.setPassword("123456");
        user.setRole(UserRole.MANAGER);
        user.setActive(true);

        userResponse = new UserResponse(
                userId,
                "john_manager",
                "john.manager@timegrid.test",
                UserRole.MANAGER,
                true,
                null
        );
    }

    @Test
    void createUser_shouldReturnCreatedUser() throws Exception {
        String requestBody = """
                {
                  "username": "john_manager",
                  "email": "john.manager@timegrid.test",
                  "password": "123456",
                  "role": "MANAGER"
                }
                """;

        when(userMapper.toEntity(any(UserRequest.class))).thenReturn(user);
        when(userService.createUser(user)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.username").value("john_manager"))
                .andExpect(jsonPath("$.email").value("john.manager@timegrid.test"))
                .andExpect(jsonPath("$.password").doesNotExist());

        verify(userService).createUser(user);
    }

    @Test
    void createUser_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                  "username": "",
                  "email": "invalid-email",
                  "password": "",
                  "role": null
                }
                """;

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fields.username").exists())
                .andExpect(jsonPath("$.fields.email").exists())
                .andExpect(jsonPath("$.fields.password").exists())
                .andExpect(jsonPath("$.fields.role").exists());

        verify(userService, never()).createUser(any());
    }

    @Test
    void findById_shouldReturnUserWhenUserExists() throws Exception {
        when(userService.findById(userId)).thenReturn(user);
        when(userMapper.toResponse(user)).thenReturn(userResponse);

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void findById_shouldReturnBadRequestWhenBusinessExceptionIsThrown() throws Exception {
        when(userService.findById(userId)).thenThrow(new BusinessException("Usuario nao encontrado."));

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Usuario nao encontrado."))
                .andExpect(jsonPath("$.path").value("/users/" + userId));
    }

    @Test
    void listUsers_shouldReturnUsers() throws Exception {
        when(userService.listUsers()).thenReturn(List.of(user));
        when(userMapper.toResponseList(List.of(user))).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(userId.toString()))
                .andExpect(jsonPath("$[0].username").value("john_manager"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        String requestBody = """
                {
                  "username": "john_updated",
                  "email": "john.updated@timegrid.test",
                  "role": "ADMIN"
                }
                """;

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setUsername("john_updated");
        updatedUser.setEmail("john.updated@timegrid.test");
        updatedUser.setRole(UserRole.ADMIN);
        updatedUser.setActive(true);

        UserResponse updatedResponse = new UserResponse(
                userId,
                "john_updated",
                "john.updated@timegrid.test",
                UserRole.ADMIN,
                true,
                null
        );

        when(userMapper.toEntity(any(UserUpdateRequest.class))).thenReturn(updatedUser);
        when(userService.updateUser(eq(userId), eq(updatedUser))).thenReturn(updatedUser);
        when(userMapper.toResponse(updatedUser)).thenReturn(updatedResponse);

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("john_updated"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void updateUser_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                  "username": "",
                  "email": "invalid-email",
                  "role": null
                }
                """;

        mockMvc.perform(put("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fields.username").exists())
                .andExpect(jsonPath("$.fields.email").exists())
                .andExpect(jsonPath("$.fields.role").exists())
                .andExpect(jsonPath("$.fields.password").doesNotExist());

        verify(userService, never()).updateUser(any(), any());
    }

    @Test
    void changeOwnPassword_shouldReturnNoContent() throws Exception {
        String requestBody = """
                {
                  "currentPassword": "123456",
                  "newPassword": "new-password"
                }
                """;

        mockMvc.perform(patch("/users/me/password")
                        .principal(() -> "john.manager@timegrid.test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        verify(userService).changeOwnPassword(
                "john.manager@timegrid.test",
                "123456",
                "new-password"
        );
    }

    @Test
    void changeOwnPassword_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                  "currentPassword": "",
                  "newPassword": ""
                }
                """;

        mockMvc.perform(patch("/users/me/password")
                        .principal(() -> "john.manager@timegrid.test")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.currentPassword").exists())
                .andExpect(jsonPath("$.fields.newPassword").exists());

        verify(userService, never()).changeOwnPassword(any(), any(), any());
    }

    @Test
    void resetPassword_shouldReturnNoContent() throws Exception {
        String requestBody = """
                {
                  "newPassword": "manager-defined-password"
                }
                """;

        mockMvc.perform(patch("/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isNoContent());

        verify(userService).resetPassword(userId, "manager-defined-password");
    }

    @Test
    void resetPassword_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                  "newPassword": ""
                }
                """;

        mockMvc.perform(patch("/users/{id}/password", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.newPassword").exists());

        verify(userService, never()).resetPassword(any(), any());
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(userId);
    }

    @Test
    void setActive_shouldReturnNoContent() throws Exception {
        mockMvc.perform(patch("/users/{id}/active", userId)
                        .param("active", "false"))
                .andExpect(status().isNoContent());

        verify(userService).setActive(userId, false);
    }
}

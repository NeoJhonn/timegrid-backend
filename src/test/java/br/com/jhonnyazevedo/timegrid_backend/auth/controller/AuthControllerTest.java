package br.com.jhonnyazevedo.timegrid_backend.auth.controller;

import br.com.jhonnyazevedo.timegrid_backend.auth.dto.LoginRequest;
import br.com.jhonnyazevedo.timegrid_backend.auth.dto.LoginResponse;
import br.com.jhonnyazevedo.timegrid_backend.auth.service.AuthService;
import br.com.jhonnyazevedo.timegrid_backend.exception.BusinessException;
import br.com.jhonnyazevedo.timegrid_backend.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        AuthController authController = new AuthController(authService);
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void login_shouldReturnTokenWhenCredentialsAreValid() throws Exception {
        String requestBody = """
                {
                  "email": "john.manager@timegrid.test",
                  "password": "123456"
                }
                """;

        when(authService.login(any(LoginRequest.class))).thenReturn(new LoginResponse("jwt-token"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"));
    }

    @Test
    void login_shouldReturnBadRequestWhenRequestIsInvalid() throws Exception {
        String requestBody = """
                {
                  "email": "invalid-email",
                  "password": ""
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fields.email").exists())
                .andExpect(jsonPath("$.fields.password").exists());

        verify(authService, never()).login(any());
    }

    @Test
    void login_shouldReturnBadRequestWhenBusinessExceptionIsThrown() throws Exception {
        String requestBody = """
                {
                  "email": "john.manager@timegrid.test",
                  "password": "wrong-password"
                }
                """;

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new BusinessException("Email ou senha invalidos."));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email ou senha invalidos."));
    }
}

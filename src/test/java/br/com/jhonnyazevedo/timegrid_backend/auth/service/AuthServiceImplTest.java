package br.com.jhonnyazevedo.timegrid_backend.auth.service;

import br.com.jhonnyazevedo.timegrid_backend.auth.dto.LoginRequest;
import br.com.jhonnyazevedo.timegrid_backend.auth.dto.LoginResponse;
import br.com.jhonnyazevedo.timegrid_backend.enums.UserRole;
import br.com.jhonnyazevedo.timegrid_backend.exception.BusinessException;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User user;
    private LoginRequest request;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("john_manager");
        user.setEmail("john.manager@timegrid.test");
        user.setPassword("encoded-password");
        user.setRole(UserRole.MANAGER);
        user.setActive(true);

        request = new LoginRequest("john.manager@timegrid.test", "123456");
    }

    @Test
    void login_shouldReturnTokenWhenCredentialsAreValid() {
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        LoginResponse response = authService.login(request);

        assertEquals("jwt-token", response.token());
        verify(jwtService).generateToken(user);
    }

    @Test
    void login_shouldThrowBusinessExceptionWhenEmailDoesNotExist() {
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder, never()).matches(request.password(), user.getPassword());
        verify(jwtService, never()).generateToken(user);
    }

    @Test
    void login_shouldThrowBusinessExceptionWhenUserIsInactive() {
        user.setActive(false);

        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));

        assertThrows(
                BusinessException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder, never()).matches(request.password(), user.getPassword());
        verify(jwtService, never()).generateToken(user);
    }

    @Test
    void login_shouldThrowBusinessExceptionWhenPasswordDoesNotMatch() {
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        assertThrows(
                BusinessException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateToken(user);
    }
}

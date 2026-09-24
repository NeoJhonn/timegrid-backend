package br.com.jhonnyazevedo.timegrid_backend.auth.service;

import br.com.jhonnyazevedo.timegrid_backend.auth.dto.LoginRequest;
import br.com.jhonnyazevedo.timegrid_backend.auth.dto.LoginResponse;
import br.com.jhonnyazevedo.timegrid_backend.auth.dto.RefreshTokenRequest;
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
    void login_shouldReturnTokensWhenCredentialsAreValid() {
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(true);
        when(jwtService.generateAccessToken(user)).thenReturn("access-token");
        when(jwtService.generateRefreshToken(user)).thenReturn("refresh-token");

        LoginResponse response = authService.login(request);

        assertEquals("access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
        verify(jwtService).generateAccessToken(user);
        verify(jwtService).generateRefreshToken(user);
    }

    @Test
    void login_shouldThrowBusinessExceptionWhenEmailDoesNotExist() {
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> authService.login(request)
        );

        verify(passwordEncoder, never()).matches(request.password(), user.getPassword());
        verify(jwtService, never()).generateAccessToken(user);
        verify(jwtService, never()).generateRefreshToken(user);
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
        verify(jwtService, never()).generateAccessToken(user);
        verify(jwtService, never()).generateRefreshToken(user);
    }

    @Test
    void login_shouldThrowBusinessExceptionWhenPasswordDoesNotMatch() {
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password(), user.getPassword())).thenReturn(false);

        assertThrows(
                BusinessException.class,
                () -> authService.login(request)
        );

        verify(jwtService, never()).generateAccessToken(user);
        verify(jwtService, never()).generateRefreshToken(user);
    }

    @Test
    void refresh_shouldReturnNewTokensWhenRefreshTokenIsValid() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("refresh-token");

        when(jwtService.extractEmailFromRefreshToken("refresh-token"))
                .thenReturn(Optional.of("john.manager@timegrid.test"));
        when(userRepository.findByEmail("john.manager@timegrid.test")).thenReturn(Optional.of(user));
        when(jwtService.generateAccessToken(user)).thenReturn("new-access-token");

        LoginResponse response = authService.refresh(refreshRequest);

        assertEquals("new-access-token", response.accessToken());
        assertEquals("refresh-token", response.refreshToken());
    }

    @Test
    void refresh_shouldThrowBusinessExceptionWhenRefreshTokenIsInvalid() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("invalid-token");

        when(jwtService.extractEmailFromRefreshToken("invalid-token")).thenReturn(Optional.empty());

        assertThrows(
                BusinessException.class,
                () -> authService.refresh(refreshRequest)
        );

        verify(userRepository, never()).findByEmail("john.manager@timegrid.test");
    }

    @Test
    void refresh_shouldThrowBusinessExceptionWhenUserIsInactive() {
        RefreshTokenRequest refreshRequest = new RefreshTokenRequest("refresh-token");
        user.setActive(false);

        when(jwtService.extractEmailFromRefreshToken("refresh-token"))
                .thenReturn(Optional.of("john.manager@timegrid.test"));
        when(userRepository.findByEmail("john.manager@timegrid.test")).thenReturn(Optional.of(user));

        assertThrows(
                BusinessException.class,
                () -> authService.refresh(refreshRequest)
        );

        verify(jwtService, never()).generateAccessToken(user);
        verify(jwtService, never()).generateRefreshToken(user);
    }
}

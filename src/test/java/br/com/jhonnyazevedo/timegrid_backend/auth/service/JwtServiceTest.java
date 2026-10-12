package br.com.jhonnyazevedo.timegrid_backend.auth.service;

import br.com.jhonnyazevedo.timegrid_backend.enums.UserRole;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private JwtService jwtService;
    private User user;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService("test-secret", 60, 1440);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setUsername("john_manager");
        user.setEmail("john.manager@timegrid.test");
        user.setRole(UserRole.MANAGER);
    }

    @Test
    void generateAccessToken_shouldCreateTokenWithUserEmailAsSubject() {
        String token = jwtService.generateAccessToken(user);

        Optional<String> email = jwtService.extractEmail(token);

        assertEquals(Optional.of("john.manager@timegrid.test"), email);
    }

    @Test
    void generateRefreshToken_shouldCreateRefreshTokenWithUserEmailAsSubject() {
        String token = jwtService.generateRefreshToken(user);

        Optional<String> email = jwtService.extractEmailFromRefreshToken(token);

        assertEquals(Optional.of("john.manager@timegrid.test"), email);
    }

    @Test
    void extractEmail_shouldReturnEmptyWhenTokenIsRefreshToken() {
        String token = jwtService.generateRefreshToken(user);

        Optional<String> email = jwtService.extractEmail(token);

        assertTrue(email.isEmpty());
    }

    @Test
    void extractEmail_shouldReturnEmptyWhenTokenIsInvalid() {
        Optional<String> email = jwtService.extractEmail("invalid-token");

        assertTrue(email.isEmpty());
    }
}

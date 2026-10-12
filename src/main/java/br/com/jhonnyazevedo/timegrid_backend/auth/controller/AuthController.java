package br.com.jhonnyazevedo.timegrid_backend.auth.controller;

import br.com.jhonnyazevedo.timegrid_backend.auth.dto.LoginRequest;
import br.com.jhonnyazevedo.timegrid_backend.auth.dto.LoginResponse;
import br.com.jhonnyazevedo.timegrid_backend.auth.dto.RefreshTokenRequest;
import br.com.jhonnyazevedo.timegrid_backend.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Endpoints para login e renovacao de tokens JWT")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    @Operation(summary = "Realiza login", description = "Autentica o usuario por email e senha, retornando access token e refresh token.")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Renova o access token", description = "Recebe um refresh token valido e retorna um novo access token, mantendo o mesmo refresh token ate ele expirar.")
    public ResponseEntity<LoginResponse> refresh(@RequestBody @Valid RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
}

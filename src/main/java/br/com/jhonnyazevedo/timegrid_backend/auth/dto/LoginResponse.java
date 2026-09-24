package br.com.jhonnyazevedo.timegrid_backend.auth.dto;

public record LoginResponse(
        String accessToken,
        String refreshToken
) {
}

package br.com.jhonnyazevedo.timegrid_backend.user.dto;

import br.com.jhonnyazevedo.timegrid_backend.enums.UserRole;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserResponse(
        UUID id,
        String username,
        String email,
        UserRole role,
        Boolean active,
        LocalDateTime createdAt
) {
}
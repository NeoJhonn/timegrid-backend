package br.com.jhonnyazevedo.timegrid_backend.client.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ClientResponse(
        UUID id,
        String name,
        String phone,
        UUID userId,
        LocalDateTime createdAt
) {
}
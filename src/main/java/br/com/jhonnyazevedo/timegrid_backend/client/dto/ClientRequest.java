package br.com.jhonnyazevedo.timegrid_backend.client.dto;

import jakarta.validation.constraints.NotBlank;

public record ClientRequest(
        @NotBlank(message = "Nome do cliente é obrigatório")
        String name,

        @NotBlank(message = "Telefone do cliente é obrigatório")
        String phone
) {
}
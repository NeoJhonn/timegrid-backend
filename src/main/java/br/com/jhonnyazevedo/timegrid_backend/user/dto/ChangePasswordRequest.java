package br.com.jhonnyazevedo.timegrid_backend.user.dto;

import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @NotBlank(message = "Senha atual e obrigatoria")
        String currentPassword,

        @NotBlank(message = "Nova senha e obrigatoria")
        String newPassword
) {
}

package br.com.jhonnyazevedo.timegrid_backend.user.dto;

import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "Nova senha e obrigatoria")
        String newPassword
) {
}

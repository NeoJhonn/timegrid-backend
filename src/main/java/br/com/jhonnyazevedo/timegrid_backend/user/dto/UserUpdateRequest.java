package br.com.jhonnyazevedo.timegrid_backend.user.dto;

import br.com.jhonnyazevedo.timegrid_backend.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateRequest(
        @NotBlank(message = "Username e obrigatorio")
        String username,

        @NotBlank(message = "Email e obrigatorio")
        @Email(message = "Email invalido")
        String email,

        @NotNull(message = "Role e obrigatoria")
        UserRole role
) {
}

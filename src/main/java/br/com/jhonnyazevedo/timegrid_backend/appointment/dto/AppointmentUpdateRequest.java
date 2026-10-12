package br.com.jhonnyazevedo.timegrid_backend.appointment.dto;

import br.com.jhonnyazevedo.timegrid_backend.enums.TimeGrid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AppointmentUpdateRequest(
        @NotNull(message = "Horário final é obrigatório")
        TimeGrid endTime,

        @NotBlank(message = "Serviço é obrigatório")
        String service
) {
}
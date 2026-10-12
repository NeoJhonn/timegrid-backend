package br.com.jhonnyazevedo.timegrid_backend.appointment.dto;

import br.com.jhonnyazevedo.timegrid_backend.enums.TimeGrid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.UUID;

public record AppointmentRequest(
        @NotNull(message = "Cliente é obrigatório")
        UUID clientId,

        @NotBlank(message = "Serviço é obrigatório")
        String service,

        @NotNull(message = "Data do agendamento é obrigatória")
        @FutureOrPresent(message = "Data do agendamento não pode estar no passado")
        LocalDate appointmentDate,

        @NotNull(message = "Horário inicial é obrigatório")
        TimeGrid startTime,

        @NotNull(message = "Horário final é obrigatório")
        TimeGrid endTime
) {
}
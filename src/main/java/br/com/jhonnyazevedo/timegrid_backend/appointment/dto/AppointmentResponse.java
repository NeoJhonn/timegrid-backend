package br.com.jhonnyazevedo.timegrid_backend.appointment.dto;

import br.com.jhonnyazevedo.timegrid_backend.enums.TimeGrid;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record AppointmentResponse(
        UUID id,
        UUID userId,
        UUID clientId,
        String clientName,
        String service,
        LocalDate appointmentDate,
        TimeGrid startTime,
        TimeGrid endTime,
        LocalDateTime createdAt
) {
}
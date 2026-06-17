package br.com.jhonnyazevedo.timegrid_backend.appointment.controller;

import br.com.jhonnyazevedo.timegrid_backend.appointment.dto.AppointmentRequest;
import br.com.jhonnyazevedo.timegrid_backend.appointment.dto.AppointmentResponse;
import br.com.jhonnyazevedo.timegrid_backend.appointment.dto.AppointmentUpdateRequest;
import br.com.jhonnyazevedo.timegrid_backend.appointment.entity.Appointment;
import br.com.jhonnyazevedo.timegrid_backend.appointment.mapper.AppointmentMapper;
import br.com.jhonnyazevedo.timegrid_backend.appointment.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;
    private final AppointmentMapper appointmentMapper;

    @PostMapping("/users/{userId}/appointments")
    public ResponseEntity<AppointmentResponse> createAppointment(
            @PathVariable UUID userId,
            @RequestBody @Valid AppointmentRequest request
    ) {
        Appointment appointment = appointmentMapper.toEntity(request);
        Appointment createdAppointment = appointmentService.createAppointment(
                userId,
                request.clientId(),
                appointment
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(appointmentMapper.toResponse(createdAppointment));
    }

    @GetMapping("/users/{userId}/appointments")
    public ResponseEntity<List<AppointmentResponse>> listAppointmentsByDate(
            @PathVariable UUID userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<Appointment> appointments = appointmentService.listAppointmentsByDate(userId, date);
        return ResponseEntity.ok(appointmentMapper.toResponseList(appointments));
    }

    @PutMapping("/users/{userId}/appointments/{appointmentId}")
    public ResponseEntity<AppointmentResponse> updateAppointment(
            @PathVariable UUID userId,
            @PathVariable UUID appointmentId,
            @RequestBody @Valid AppointmentUpdateRequest request
    ) {
        Appointment appointment = appointmentMapper.toEntity(appointmentId, request);
        Appointment updatedAppointment = appointmentService.updateAppointment(userId, appointment);
        return ResponseEntity.ok(appointmentMapper.toResponse(updatedAppointment));
    }

    @DeleteMapping("/appointments/{appointmentId}")
    public ResponseEntity<Void> deleteAppointment(@PathVariable UUID appointmentId) {
        appointmentService.deleteAppointment(appointmentId);
        return ResponseEntity.noContent().build();
    }
}

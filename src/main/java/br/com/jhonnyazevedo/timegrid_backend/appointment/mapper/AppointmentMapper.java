package br.com.jhonnyazevedo.timegrid_backend.appointment.mapper;

import br.com.jhonnyazevedo.timegrid_backend.appointment.dto.AppointmentRequest;
import br.com.jhonnyazevedo.timegrid_backend.appointment.dto.AppointmentResponse;
import br.com.jhonnyazevedo.timegrid_backend.appointment.dto.AppointmentUpdateRequest;
import br.com.jhonnyazevedo.timegrid_backend.appointment.entity.Appointment;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class AppointmentMapper {

    public Appointment toEntity(AppointmentRequest request) {
        Appointment appointment = new Appointment();
        appointment.setService(request.service());
        appointment.setAppointmentDate(request.appointmentDate());
        appointment.setStartTime(request.startTime());
        appointment.setEndTime(request.endTime());
        return appointment;
    }

    public Appointment toEntity(UUID appointmentId, AppointmentUpdateRequest request) {
        Appointment appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setEndTime(request.endTime());
        appointment.setService(request.service());
        return appointment;
    }

    public AppointmentResponse toResponse(Appointment appointment) {
        UUID userId = appointment.getUser() != null ? appointment.getUser().getId() : null;
        UUID clientId = appointment.getClient() != null ? appointment.getClient().getId() : null;
        String clientName = appointment.getClient() != null ? appointment.getClient().getName() : null;

        return new AppointmentResponse(
                appointment.getId(),
                userId,
                clientId,
                clientName,
                appointment.getService(),
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                appointment.getCreatedAt()
        );
    }

    public List<AppointmentResponse> toResponseList(List<Appointment> appointments) {
        return appointments.stream()
                .map(this::toResponse)
                .toList();
    }
}
package br.com.jhonnyazevedo.timegrid_backend.appointment.service;

import br.com.jhonnyazevedo.timegrid_backend.appointment.entity.Appointment;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {

    Appointment createAppointment(UUID userId, UUID clientId, Appointment appointment);

    Appointment updateAppointment(UUID userId, Appointment appointment);

    List<Appointment> listAppointmentsByDate(UUID userId, LocalDate date);

    void deleteAppointment(UUID id);
}

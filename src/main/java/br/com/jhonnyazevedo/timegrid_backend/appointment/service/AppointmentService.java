package br.com.jhonnyazevedo.timegrid_backend.appointment.service;

import br.com.jhonnyazevedo.timegrid_backend.appointment.entity.Appointment;
import br.com.jhonnyazevedo.timegrid_backend.enums.TimeGrid;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AppointmentService {

    Appointment createAppointment(UUID userId, UUID clientId, Appointment appointment);

    List<Appointment> listByUser(UUID userId);

    List<Appointment> listByDate(UUID userId, LocalDate date);

    void deleteAppointment(UUID id);

    boolean isTimeAvailable(UUID userId, LocalDate date, TimeGrid startTime);
}

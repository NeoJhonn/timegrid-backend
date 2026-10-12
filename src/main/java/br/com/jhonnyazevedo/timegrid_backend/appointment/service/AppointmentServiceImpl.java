package br.com.jhonnyazevedo.timegrid_backend.appointment.service;

import br.com.jhonnyazevedo.timegrid_backend.appointment.entity.Appointment;
import br.com.jhonnyazevedo.timegrid_backend.appointment.repository.AppointmentRepository;
import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;
import br.com.jhonnyazevedo.timegrid_backend.client.repository.ClientRepository;
import br.com.jhonnyazevedo.timegrid_backend.exception.BusinessException;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final ClientRepository clientRepository;

    @Transactional
    @Override
    public Appointment createAppointment(UUID userId, UUID clientId, Appointment appointment) {

        if (appointment.getStartTime() == null || appointment.getEndTime() == null
                || appointment.getAppointmentDate() == null) {
            throw new BusinessException("Dados inválidos para agendamento");
        }

        if (appointment.getStartTime().ordinal() > appointment.getEndTime().ordinal()) {
            throw new BusinessException("Horário de início não pode ser maior que horário final");
        }

        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new BusinessException("Não é possível agendar em datas passadas");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));

        if (client.getUser() == null || !client.getUser().getId().equals(user.getId())) {
            throw new BusinessException("Cliente não pertence ao usuário logado");
        }

        boolean conflict = appointmentRepository.existsConflict(
                user,
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime(),
                null
        );

        if (conflict) {
            throw new BusinessException("Já existe um agendamento neste intervalo de horário");
        }

        appointment.setClient(client);
        appointment.setUser(user);

        return appointmentRepository.save(appointment);
    }

    @Override
    public Appointment updateAppointment(UUID userId, Appointment appointment) {
        Appointment existAppointment = appointmentRepository.findById(appointment.getId())
                .orElseThrow(() -> new BusinessException("Agendamento não encontrado"));

        if (existAppointment.getUser() == null || !existAppointment.getUser().getId().equals(userId)) {
            throw new BusinessException("Agendamento não pertence ao usuário");
        }

        if (appointment.getEndTime() == null || appointment.getService() == null
                || appointment.getService().isBlank()) {
            throw new BusinessException("Horário final e serviço são obrigatórios");
        }

        if (existAppointment.getStartTime().ordinal() > appointment.getEndTime().ordinal()) {
            throw new BusinessException("Horário de início não pode ser maior que horário final");
        }

        boolean conflict = appointmentRepository.existsConflict(
                existAppointment.getUser(),
                existAppointment.getAppointmentDate(),
                existAppointment.getStartTime(),
                appointment.getEndTime(),
                existAppointment.getId()
        );

        if (conflict) {
            throw new BusinessException("Já existe um agendamento neste intervalo de horário");
        }

        existAppointment.setEndTime(appointment.getEndTime());
        existAppointment.setService(appointment.getService());

        return appointmentRepository.save(existAppointment);
    }

    @Override
    public List<Appointment> listAppointmentsByDate(UUID userId, LocalDate date) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        return appointmentRepository.findByUserAndAppointmentDate(user, date);
    }

    @Override
    public void deleteAppointment(UUID userId, UUID appointmentId) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new BusinessException("Agendamento não encontrado"));

        if (appointment.getUser() == null || !appointment.getUser().getId().equals(userId)) {
            throw new BusinessException("Agendamento não pertence ao usuário");
        }

        appointmentRepository.delete(appointment);
    }
}

package br.com.jhonnyazevedo.timegrid_backend.appointment.service;

import br.com.jhonnyazevedo.timegrid_backend.appointment.entity.Appointment;
import br.com.jhonnyazevedo.timegrid_backend.appointment.repository.AppointmentRepository;
import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;
import br.com.jhonnyazevedo.timegrid_backend.client.repository.ClientRepository;
import br.com.jhonnyazevedo.timegrid_backend.enums.TimeGrid;
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

        // 🚨 Validação básica
        if (appointment.getStartTime() == null || appointment.getEndTime() == null || appointment.getAppointmentDate() == null) {
            throw new RuntimeException("Dados de agendamento inválidos.");
        }

        // ⏱ Regra de horário
        if (appointment.getStartTime().ordinal() >= appointment.getEndTime().ordinal()) {
            throw new RuntimeException("Horário inicial deve ser menor que o final.");
        }

        // 📅 Data passada
        if (appointment.getAppointmentDate().isBefore(LocalDate.now())) {
            throw new RuntimeException("Não é possível agendar para datas passadas.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado."));

        // 🔒 Segurança
        if (client.getUser() == null || !client.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Cliente não pertence ao usuário.");
        }

        // 🚫 Conflito direto no banco (PERFEITO)
        boolean conflict = appointmentRepository.existsConflict(
                user,
                appointment.getAppointmentDate(),
                appointment.getStartTime(),
                appointment.getEndTime()
        );

        if (conflict) {
            throw new RuntimeException("Horário já está ocupado (conflito de intervalo).");
        }

        appointment.setUser(user);
        appointment.setClient(client);

        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> listByUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        return appointmentRepository.findByUser(user);
    }

    @Override
    public List<Appointment> listByDate(UUID userId, LocalDate date) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        return appointmentRepository.findByUserAndAppointmentDate(user, date);
    }

    @Override
    public void deleteAppointment(UUID id) {
        appointmentRepository.deleteById(id);
    }

    @Override
    public boolean isTimeAvailable(UUID userId, LocalDate date, TimeGrid startTime) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

        return !appointmentRepository.existsByUserAndAppointmentDateAndStartTime(
                user, date, startTime
        );
    }

    private void validateTimeConflict(User user, Appointment newAppointment) {

        List<Appointment> appointments = appointmentRepository
                .findByUserAndAppointmentDate(user, newAppointment.getAppointmentDate());

        for (Appointment existing : appointments) {

            boolean overlap =
                    newAppointment.getStartTime().ordinal() < existing.getEndTime().ordinal()
                            &&
                            newAppointment.getEndTime().ordinal() > existing.getStartTime().ordinal();

            if (overlap) {
                throw new RuntimeException("Horário já está ocupado (conflito de intervalo).");
            }
        }
    }
}

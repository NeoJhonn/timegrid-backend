package br.com.jhonnyazevedo.timegrid_backend.appointment.repository;

import br.com.jhonnyazevedo.timegrid_backend.appointment.entity.Appointment;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.enums.TimeGrid;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    /**
     * Lista todos os agendamentos de um usuário.
     */
    List<Appointment> findByUser(User user);

    /**
     * Busca todos os agendadmentos de um usuário em uma data específica.
     * Vamos usar para montar a agenda do dia
     */
    List<Appointment> findByUserAndAppointmentDate(User user, LocalDate appointmentDate);


    /**
     * Busca um agendamento específico pelo horário de inicio
     * Pode ser usado como validação extra( não ter duplicidade de horários)
     */
    List<Appointment> findByUserAndAppointmentDateAndStartTime(
            User user, LocalDate appointmentDate, TimeGrid startTime);

    /**
     * Verifica se já existe um agendamento exatamente no mesmo horário.
     * reforça constraint do banco.
     */
    boolean existsByUserAndAppointmentDateAndStartTime(
            User user, LocalDate appointmentDate, TimeGrid startTime
    );

    /**
     * Verifica se há conflito de intervalos
     */
    @Query("""
    SELECT COUNT(a) > 0 FROM Appointment a
    WHERE a.user = :user
    AND a.appointmentDate = :date
    AND a.startTime < :endTime
    AND a.endTime > :startTime
""")
    boolean existsConflict(
            @Param("user") User user,
            @Param("date") LocalDate date,
            @Param("startTime") TimeGrid startTime,
            @Param("endTime") TimeGrid endTime
    );
}

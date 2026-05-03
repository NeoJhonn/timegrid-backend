package br.com.jhonnyazevedo.timegrid_backend.appointment.repository;

import br.com.jhonnyazevedo.timegrid_backend.appointment.entity.Appointment;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.enums.TimeGrid;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    /**
     * Busca todos os agendadmentos de um usuário em uma data específica.
     * Vamos usar para montar a agenda do dia
     */
    List<Appointment> findByUserAndAppointmentDate(User user, LocalDate appointmentDate);

    /**
     * Verifica se há conflito de intervalos
     */
    @Query("""
    SELECT COUNT(a) > 0 FROM Appointment a
    WHERE a.user = :user
    AND a.appointmentDate = :date
    AND a.startTime <= :endTime
    AND a.endTime >= :startTime
    AND (:appointmentId IS NULL OR a.id <> :appointmentId)
""")
    boolean existsConflict(
            @Param("user") User user,
            @Param("date") LocalDate date,
            @Param("startTime") TimeGrid startTime,
            @Param("endTime") TimeGrid endTime,
            @Param("appointmentId") UUID appointmentId
    );
}

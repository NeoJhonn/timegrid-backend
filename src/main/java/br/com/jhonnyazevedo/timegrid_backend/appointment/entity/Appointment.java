package br.com.jhonnyazevedo.timegrid_backend.appointment.entity;


import br.com.jhonnyazevedo.timegrid_backend.client.entity.Client;
import br.com.jhonnyazevedo.timegrid_backend.user.entity.User;
import br.com.jhonnyazevedo.timegrid_backend.enums.TimeGrid;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "appointments",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "appointmentDate", "startTime"})
        }
)
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // Relacionamento: muitos appointments pertencem a um user
    // Tipo de carregamento Lazy, melhor em produção
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // Relacionamento: muitos appointments pertencem a um client
    // Tipo de carregamento Lazy, melhor em produção
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(nullable = false)
    private String service;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeGrid startTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TimeGrid endTime;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

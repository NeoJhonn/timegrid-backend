package br.com.jhonnyazevdo.timegrid_backend.entity;


import br.com.jhonnyazevdo.timegrid_backend.enums.TimeGrid;
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
@Table(name = "appointment")
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
    private LocalDate appointmenteDate;

    @Enumerated(EnumType.STRING)
    private TimeGrid startTime;

    @Enumerated(EnumType.STRING)
    private TimeGrid endTime;

    @CreationTimestamp
    private LocalDateTime createdAt;
}

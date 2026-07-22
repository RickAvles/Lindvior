package com.rick.smartparkingplatform.entity;

import com.rick.smartparkingplatform.enums.ParkingSpotType;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "parking_spot")
public class ParkingSpot {

    // Identificador da vaga.
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // Código identificador da vaga.
    @Column(nullable = false)
    private String code;

    // Tipo da vaga.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParkingSpotType type;

    // Status da vaga.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusParkingSpot status;

    // Indica se a vaga está ativa.
    @Column(nullable = false)
    private boolean active;

    // Data de criação.
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Setor ao qual a vaga pertence.
    @ManyToOne
    @JoinColumn(name = "parking_sector_id", nullable = false)
    private ParkingSector parkingSector;

    // Sessões da vaga.
    @OneToMany(mappedBy = "parkingSpot")
    private List<ParkingSession> parkingSessions;

}
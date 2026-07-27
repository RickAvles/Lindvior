package com.rick.smartparkingplatform.entity;

import com.rick.smartparkingplatform.enums.StatusParkingSession;
import com.rick.smartparkingplatform.simulation.gate.Gate;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "parking_session")
public class ParkingSession {

    // Identificador da sessão.
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // Data e hora de entrada.
    @Column(nullable = false)
    private LocalDateTime entryTime;

    // Data e hora de saída.
    @Column
    private LocalDateTime exitTime;

    // Status da sessão.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusParkingSession status;

    @Transient
    private Gate entryGate;

    @Transient
    private Gate exitGate;

    // Data de criação.
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Vaga utilizada na sessão.
    @ManyToOne
    @JoinColumn(name = "parking_spot_id", nullable = false)
    private ParkingSpot parkingSpot;

    // Veículo da sessão.
    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

}
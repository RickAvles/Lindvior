package com.rick.smartparkingplatform.entity;

import com.rick.smartparkingplatform.enums.VehicleType;
import com.rick.smartparkingplatform.simulation.parking.stay.StayProfile;
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
@Table(name = "vehicle")
public class Vehicle {

    // Identificador do veículo.
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // Placa do veículo.
    @Column(name = "license_plate", nullable = false, unique = true)
    private String licensePlate;

    // Tipo do veículo.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    // Cor do veículo.
    @Column(nullable = false)
    private String color;

    // Perfil de permanência do veículo.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StayProfile stayProfile;

    // Indica se o veículo está ativo.
    @Column(nullable = false)
    private boolean active = true;

    // Data de criação.
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Sessões do veículo.
    @OneToMany(mappedBy = "vehicle")
    private List<ParkingSession> parkingSessions;

}
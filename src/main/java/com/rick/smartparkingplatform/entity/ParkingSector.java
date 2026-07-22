package com.rick.smartparkingplatform.entity;

import com.rick.smartparkingplatform.enums.ParkingSectorType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "parking_sector")
@Getter
@Setter
@NoArgsConstructor
public class ParkingSector {

    // Identificador do setor.
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // Nome do setor.
    @Column(nullable = false)
    private String name;

    // Tipo do setor.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ParkingSectorType type;

    // Andar onde o setor está localizado.
    @Column(nullable = false)
    private Integer floor;

    // Indica se o setor está ativo.
    @Column(nullable = false)
    private boolean active = true;

    // Data de criação.
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Estacionamento ao qual o setor pertence.
    @ManyToOne
    @JoinColumn(name = "parking_id", nullable = false)
    private Parking parking;

    // Vagas pertencentes ao setor.
    @OneToMany(mappedBy = "parkingSector")
    private List<ParkingSpot> parkingSpots;

}
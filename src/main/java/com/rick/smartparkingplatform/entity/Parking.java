package com.rick.smartparkingplatform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "parking")
public class Parking {

    // Identificador do estacionamento.
    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    // Nome do estacionamento.
    @Column(nullable = false)
    private String name;

    // Endereço do estacionamento.
    @Column(nullable = false)
    private String address;

    // Horário de abertura.
    @Column(nullable = false)
    private LocalTime openingTime;

    // Horário de fechamento.
    @Column(nullable = false)
    private LocalTime closingTime;

    // Quantidade de cancelas de entrada.
    @Column(nullable = false)
    private Integer entryGates;

    // Quantidade de cancelas de saída.
    @Column(nullable = false)
    private Integer exitGates;

    // Tempo mínimo da cancela de entrada.
    @Column(nullable = false)
    private Integer entryGateMinProcessingSeconds;

    // Tempo máximo da cancela de entrada.
    @Column(nullable = false)
    private Integer entryGateMaxProcessingSeconds;

    // Tempo mínimo da cancela de saída.
    @Column(nullable = false)
    private Integer exitGateMinProcessingSeconds;

    // Tempo máximo da cancela de saída.
    @Column(nullable = false)
    private Integer exitGateMaxProcessingSeconds;

    // Indica se o estacionamento está ativo.
    @Column(nullable = false)
    private boolean active = true;

    // Data de criação.
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Setores do estacionamento.
    @OneToMany(mappedBy = "parking")
    private List<ParkingSector> parkingSectors;

}
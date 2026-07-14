package com.rick.smartparkingplatform.entity;

import com.rick.smartparkingplatform.enums.VehicleType;
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
@Table(
        name = "vehicle",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "license_plate")
        }
)
public class Vehicle {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "license_plate", nullable = false)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType type;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "vehicle")
    private List<ParkingSession> parkingSessions;

}
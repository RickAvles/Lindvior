package com.rick.smartparkingplatform.entity;

import com.rick.smartparkingplatform.enums.SectorType;
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

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SectorType type;

    @Column(nullable = false)
    private Integer floor;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "parking_id", nullable = false)
    private Parking parking;

    @OneToMany(mappedBy = "parkingSector")
    private List<ParkingSpot> parkingSpots;
}
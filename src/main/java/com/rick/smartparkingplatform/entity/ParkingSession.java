package com.rick.smartparkingplatform.entity;

import com.rick.smartparkingplatform.enums.StatusParkingSession;
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

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime entryTime;

    @Column
    private LocalDateTime exitTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusParkingSession status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "parking_spot_id", nullable = false)
    private ParkingSpot parkingSpot;

    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

}


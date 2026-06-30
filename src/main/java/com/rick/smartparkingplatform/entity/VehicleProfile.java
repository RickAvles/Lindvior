package com.rick.smartparkingplatform.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "vehicle_profile")
public class VehicleProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String licensePlate;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private boolean registered;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "vehicleProfile")
    private List<ParkingSession> parkingSession;

    @OneToMany(mappedBy = "vehicleProfile")
    private List<Notification> notification;

}

package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.dashboard.DashboardLayout;
import com.rick.smartparkingplatform.dto.dashboard.DashboardSector;
import com.rick.smartparkingplatform.dto.dashboard.DashboardSpot;
import com.rick.smartparkingplatform.dto.dashboard.DashboardVehicle;
import com.rick.smartparkingplatform.dto.response.DashboardResponse;
import com.rick.smartparkingplatform.entity.ParkingSector;
import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.enums.StatusParkingSpot;
import com.rick.smartparkingplatform.mapper.DashboardResponseFactory;
import com.rick.smartparkingplatform.simulation.dashboard.DashboardState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardStateService dashboardStateService;
    private final DashboardResponseFactory dashboardResponseFactory;
    private final ParkingSectorService parkingSectorService;
    private final ParkingSpotService parkingSpotService;
    private final ParkingSessionService parkingSessionService;

    // Retorna os dados da dashboard em tempo real.
    public DashboardResponse getDashboard() {

        DashboardState dashboard = dashboardStateService.getState();

        return dashboardResponseFactory.create(dashboard);
    }

    // Retorna o layout completo do estacionamento.
    public DashboardLayout getLayout() {

        List<ParkingSector> parkingSectors = parkingSectorService.findAllActive();

        List<ParkingSpot> parkingSpots = parkingSpotService.findAllActive();

        List<ParkingSession> activeSessions = parkingSessionService.getActiveSessionsDashboard();

        Map<UUID, List<ParkingSpot>> parkingSpotsBySector = parkingSpots.stream()
                .collect(Collectors.groupingBy(
                        spot -> spot.getParkingSector().getId()
                ));

        Map<UUID, ParkingSession> activeParkingSessionsBySpot = activeSessions.stream()
                .collect(Collectors.toMap(
                        session -> session.getParkingSpot().getId(),
                        Function.identity()
                ));

        return new DashboardLayout(buildSectors(
                parkingSectors,
                parkingSpotsBySector,
                activeParkingSessionsBySpot
        )
        );
    }

    // Constrói os setores do layout.
    private List<DashboardSector> buildSectors(List<ParkingSector> parkingSectors, Map<UUID, List<ParkingSpot>> spotsBySector, Map<UUID, ParkingSession> activeSessionsBySpot) {

        return parkingSectors.stream()
                .map(sector -> buildSector(
                        sector,
                        spotsBySector,
                        activeSessionsBySpot
                ))
                .toList();
    }

    // Constrói um setor do layout.
    private DashboardSector buildSector(ParkingSector parkingSector, Map<UUID, List<ParkingSpot>> spotsBySector, Map<UUID, ParkingSession> activeSessionsBySpot) {

        List<ParkingSpot> parkingSpots = spotsBySector.getOrDefault(
                parkingSector.getId(),
                List.of()
        );

        return new DashboardSector(
                parkingSector.getId(),
                parkingSector.getName(),
                calculateOccupancy(parkingSpots),
                buildSpots(
                        parkingSpots,
                        activeSessionsBySpot
                )
        );
    }

    // Constrói as vagas do setor.
    private List<DashboardSpot> buildSpots(List<ParkingSpot> parkingSpots, Map<UUID, ParkingSession> activeSessionsBySpot) {

        return parkingSpots.stream()
                .map(spot -> buildSpot(
                        spot,
                        activeSessionsBySpot
                ))
                .toList();
    }

    // Constrói uma vaga do layout.
    private DashboardSpot buildSpot(ParkingSpot parkingSpot, Map<UUID, ParkingSession> activeSessionsBySpot) {

        ParkingSession parkingSession = activeSessionsBySpot.get(parkingSpot.getId());

        return new DashboardSpot(
                parkingSpot.getId(),
                parkingSpot.getCode(),
                parkingSpot.getType(),
                parkingSpot.isActive(),
                buildVehicle(parkingSession)
        );
    }

    // Constrói o veículo estacionado na vaga.
    private DashboardVehicle buildVehicle(ParkingSession parkingSession) {

        if (parkingSession == null) {
            return null;
        }

        Vehicle vehicle = parkingSession.getVehicle();

        return new DashboardVehicle(
                vehicle.getLicensePlate(),
                vehicle.getType(),
                vehicle.getColor(),
                parkingSession.getEntryTime()
        );
    }

    // Calcula a taxa de ocupação do setor.
    private double calculateOccupancy(List<ParkingSpot> parkingSpots) {

        if (parkingSpots.isEmpty()) {
            return 0.0;
        }

        long occupiedSpots = parkingSpots.stream()
                .filter(spot ->
                        spot.getStatus() == StatusParkingSpot.OCCUPIED
                                || spot.getStatus() == StatusParkingSpot.RESERVED
                )
                .count();

        double occupancyRate =
                ((double) occupiedSpots / parkingSpots.size()) * 100;

        return BigDecimal.valueOf(occupancyRate)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
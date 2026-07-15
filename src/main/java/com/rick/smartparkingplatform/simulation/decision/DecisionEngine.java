package com.rick.smartparkingplatform.simulation.decision;

import com.rick.smartparkingplatform.dto.response.OccupancyResponse;
import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.service.ParkingEntryService;
import com.rick.smartparkingplatform.service.ParkingExitService;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import com.rick.smartparkingplatform.simulation.clock.SimulationClock;
import com.rick.smartparkingplatform.simulation.generator.ParkingStayGenerator;
import com.rick.smartparkingplatform.simulation.generator.VehicleGenerator;
import com.rick.smartparkingplatform.simulation.service.ParkingStayManager;
import com.rick.smartparkingplatform.simulation.service.TrafficProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class DecisionEngine {

    private final ParkingEntryService parkingEntryService;
    private final ParkingExitService parkingExitService;
    private final VehicleGenerator vehicleGenerator;
    private final TrafficProfileService trafficProfileService;
    private final ParkingSpotService parkingSpotService;
    private final ParkingStayGenerator parkingStayGenerator;
    private final ParkingStayManager parkingStayManager;
    private final ParkingSessionService parkingSessionService;
    private final SimulationClock simulationClock;

    private final Random random = new Random();

    /**
     * Processa um ciclo da simulação durante o horário de funcionamento.
     */
    public void processOpenTick() {

        processScheduledExits();

        if (shouldGenerateEntry()) {

            Vehicle vehicle = vehicleGenerator.generateVehicle();

            ParkingSession parkingSession =
                    parkingEntryService.processEntry(
                            vehicle.getLicensePlate()
                    );

            Duration stayDuration =
                    parkingStayGenerator.generateStayDuration(
                            vehicle.getStayProfile()
                    );

            parkingStayManager.scheduleExit(
                    parkingSession,
                    stayDuration
            );
        }
    }


    /**
     * Determina se um novo veículo deverá entrar
     * no estacionamento neste ciclo.
     */
    private boolean shouldGenerateEntry() {

        double baseProbability = trafficProfileService.getEntryProbability();

        OccupancyResponse occupancy = parkingSpotService.getOccupancy();

        if (occupancy.availableSpots() == 0) {
            return false;
        }

        double finalProbability =
                baseProbability * calculateOccupancyFactor(occupancy);

        return random.nextDouble() < finalProbability;
    }

    /**
     * Calcula o fator de redução da probabilidade
     * de entrada conforme a ocupação do estacionamento.
     */
    private double calculateOccupancyFactor(
            OccupancyResponse occupancy) {

        return 1 - (occupancy.occupancyRate().doubleValue() / 100.0);
    }

    /**
     * Processa as saídas previstas para o ciclo atual da simulação.
     */
    private void processScheduledExits() {

        LocalDateTime currentTime = simulationClock.getCurrentTime();

        List<ParkingSession> activeSessions =
                parkingSessionService.getActiveSessions();

        for (ParkingSession parkingSession : activeSessions) {

            boolean shouldExit =
                    parkingStayManager.shouldExit(
                            parkingSession,
                            currentTime
                    );

            if (!shouldExit) {
                continue;
            }

            parkingExitService.processExit(
                    parkingSession.getId()
            );

            parkingStayManager.removeSchedule(
                    parkingSession
            );
        }
    }

    /**
     * Processa um ciclo da simulação fora do horário de funcionamento.
     */
    public void processClosedTick() {

        processScheduledExits();
    }


}
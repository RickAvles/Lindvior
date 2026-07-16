package com.rick.smartparkingplatform.simulation.decision;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.service.ParkingEntryService;
import com.rick.smartparkingplatform.service.ParkingExitService;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.simulation.clock.SimulationClock;
import com.rick.smartparkingplatform.simulation.generator.ParkingStayGenerator;
import com.rick.smartparkingplatform.simulation.generator.VehicleGenerator;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import com.rick.smartparkingplatform.simulation.service.ParkingEntryManager;
import com.rick.smartparkingplatform.simulation.service.ParkingStayManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DecisionEngine {

    private final ParkingEntryService parkingEntryService;
    private final ParkingExitService parkingExitService;
    private final ParkingEntryManager parkingEntryManager;
    private final VehicleGenerator vehicleGenerator;
    private final ParkingStayManager parkingStayManager;
    private final ParkingSessionService parkingSessionService;
    private final SimulationClock simulationClock;
    private final SimulationLogger simulationLogger;
    private final ParkingStayGenerator parkingStayGenerator;

    /**
     * Processa um ciclo da simulação durante
     * o horário de funcionamento.
     */
    public void processOpenTick() {

        processExitDecisions();

        if (!parkingEntryManager.shouldGenerateEntry()) {
            return;
        }

        Vehicle vehicle = vehicleGenerator.generateVehicle();

        parkingEntryService.processEntry(
                vehicle.getLicensePlate()
        );
    }

    /**
     * Avalia as sessões abertas e processa
     * as saídas do ciclo atual.
     */
    private void processExitDecisions() {

        LocalDateTime currentTime =
                simulationClock.getCurrentTime();

        List<ParkingSession> activeSessions =
                parkingSessionService.getActiveSessions();

        for (ParkingSession parkingSession : activeSessions) {

            if (!parkingStayManager.shouldExit(
                    parkingSession,
                    currentTime)) {
                continue;
            }

            double probability = parkingStayGenerator.calculateExitProbability(parkingSession, currentTime);

            simulationLogger.exit(

                    parkingSession
                            .getVehicle()
                            .getLicensePlate(),

                    probability
            );

            parkingExitService.processExit(
                    parkingSession.getId()
            );
        }
    }

    /**
     * Processa um ciclo da simulação durante
     * o período de fechamento.
     */
    public void processClosedTick() {

        processExitDecisions();
    }

}
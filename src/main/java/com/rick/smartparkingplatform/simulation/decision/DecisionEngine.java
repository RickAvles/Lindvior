package com.rick.smartparkingplatform.simulation.decision;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.service.ParkingEntryService;
import com.rick.smartparkingplatform.service.ParkingExitService;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import com.rick.smartparkingplatform.simulation.clock.SimulationClock;
import com.rick.smartparkingplatform.simulation.generator.ParkingStayGenerator;
import com.rick.smartparkingplatform.simulation.generator.VehicleGenerator;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import com.rick.smartparkingplatform.simulation.service.*;
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
    private final ParkingExitManager parkingExitManager;

    private final ParkingSessionService parkingSessionService;

    private final VehicleGenerator vehicleGenerator;
    private final ParkingStayManager parkingStayManager;
    private final ParkingStayGenerator parkingStayGenerator;

    private final EntryQueueService entryQueueService;
    private final ExitQueueService exitQueueService;

    private final EntryFlowManager entryFlowManager;
    private final ExitFlowManager exitFlowManager;

    private final SimulationClock simulationClock;
    private final SimulationLogger simulationLogger;

    private final EnteringQueueService enteringQueueService;

    private final ParkingArrivalManager parkingArrivalManager;

    private final ParkingSpotService parkingSpotService;

    /**
     * Processa um ciclo da simulação durante
     * o horário de funcionamento.
     */
    public void processOpenTick() {

        LocalDateTime currentTime = simulationClock.getCurrentTime();

        processExitDecisions(currentTime);

        processEntryGeneration();

        processEntryFlow(currentTime);

        processParkingFlow(currentTime);

        processExitFlow(currentTime);
    }

    /**
     * Processa um ciclo da simulação durante
     * o período de fechamento.
     */
    public void processClosedTick() {

        LocalDateTime currentTime = simulationClock.getCurrentTime();

        processExitDecisions(currentTime);

        processExitFlow(currentTime);
    }

    /**
     * Avalia as sessões abertas e move os veículos
     * que deixaram a vaga para a fila de saída.
     */
    private void processExitDecisions(LocalDateTime currentTime) {

        List<ParkingSession> activeSessions =
                parkingSessionService.getActiveSessions();

        for (ParkingSession parkingSession : activeSessions) {

            if (!parkingStayManager.shouldExit(
                    parkingSession,
                    currentTime
            )) {
                continue;
            }

            double probability =
                    parkingStayGenerator.calculateExitProbability(
                            parkingSession,
                            currentTime
                    );

            simulationLogger.exit(
                    parkingSession.getVehicle().getLicensePlate(),
                    probability
            );

            parkingExitService.processExit(parkingSession);
        }
    }

    /**
     * Gera novos veículos para a fila de entrada.
     */
    private void processEntryGeneration() {

        if (!parkingEntryManager.shouldGenerateEntry()) {
            return;
        }

        Vehicle vehicle = vehicleGenerator.generateVehicle();

        entryQueueService.enqueue(vehicle);
    }

    /**
     * Processa a entrada de veículos no estacionamento.
     */
    private void processEntryFlow(LocalDateTime currentTime) {

        if (!entryQueueService.hasVehicles()) {
            return;
        }

        if (!entryFlowManager.canProcessEntry(currentTime)) {
            return;
        }

        Vehicle vehicle = entryQueueService.dequeue();

        if (vehicle == null) {
            return;
        }

        parkingEntryService.processEntry(
                vehicle.getLicensePlate()
        );

        entryFlowManager.registerEntry(currentTime);
    }

    /**
     * Processa a saída de veículos do estacionamento.
     */
    private void processExitFlow(LocalDateTime currentTime) {

        if (!parkingExitManager.shouldProcessExit()) {
            return;
        }

        if (!exitFlowManager.canProcessExit(currentTime)) {
            return;
        }

        ParkingSession parkingSession =
                exitQueueService.dequeue();

        if (parkingSession == null) {
            return;
        }

        parkingSessionService.closeSession(parkingSession);

        simulationLogger.leave(
                parkingSession.getVehicle().getLicensePlate()
        );

        exitFlowManager.registerExit(currentTime);
    }

    /**
     * Processa os veículos que estão
     * procurando uma vaga.
     */
    /**
     * Processa os veículos que estão
     * procurando uma vaga.
     */
    private void processParkingFlow(LocalDateTime currentTime) {

        if (!enteringQueueService.hasVehicles()) {
            return;
        }

        if (!parkingArrivalManager.canPark(currentTime)) {
            return;
        }

        ParkingSession parkingSession =
                enteringQueueService.dequeue();

        if (parkingSession == null) {
            return;
        }

        parkingSpotService.occupy(
                parkingSession.getParkingSpot()
        );

        parkingSessionService.park(parkingSession);

        simulationLogger.entry(
                parkingSession.getVehicle().getLicensePlate()
        );

        parkingArrivalManager.registerParking(currentTime);
    }
    

}
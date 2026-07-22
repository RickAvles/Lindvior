package com.rick.smartparkingplatform.simulation.parking.entry;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import com.rick.smartparkingplatform.simulation.gate.EntryMovementManager;
import com.rick.smartparkingplatform.simulation.gate.Gate;
import com.rick.smartparkingplatform.simulation.parking.flow.ParkingMovementManager;
import com.rick.smartparkingplatform.simulation.queue.EntryGateQueueService;
import com.rick.smartparkingplatform.simulation.queue.EntryQueueService;
import com.rick.smartparkingplatform.simulation.queue.ParkingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParkingEntryService {

    // Arrival
    private final ArrivalManager arrivalManager;
    private final VehicleProvider vehicleProvider;

    // Gate
    private final EntryQueueService entryQueueService;
    private final EntryGateQueueService entryGateQueueService;
    private final EntryFlowManager entryFlowManager;
    private final EntryMovementManager entryMovementManager;

    // Parking
    private final ParkingQueueService parkingQueueService;
    private final ParkingMovementManager parkingMovementManager;

    // Domain
    private final ParkingSpotService parkingSpotService;
    private final ParkingSessionService parkingSessionService;

    // Simulation
    private final SimulationClock simulationClock;

    // Processa a entrada de veículos.
    @Transactional
    public void process() {

        processArrival();

        processGate();

        processGateCrossing();
    }

    // Processa a chegada de novos veículos.
    private void processArrival() {

        if (!arrivalManager.shouldGenerateVehicle()) {
            return;
        }

        Vehicle vehicle = vehicleProvider.nextVehicle();

        entryQueueService.enqueue(vehicle);
    }

    // Move um veículo da fila de entrada para a cancela.
    private void processGate() {

        if (!parkingSpotService.hasAvailableSpot()) {
            return;
        }

        if (!entryQueueService.hasWaitingVehicles()) {
            return;
        }

        Optional<Gate> availableGate = entryFlowManager.getAvailableGate();

        if (availableGate.isEmpty()) {
            return;
        }

        Vehicle vehicle = entryQueueService.dequeue();

        parkingSessionService.validateNoOpenSession(vehicle);

        ParkingSpot parkingSpot = parkingSpotService.reserveAvailableSpot();

        ParkingSession parkingSession = parkingSessionService.startEntering(
                vehicle,
                parkingSpot
        );

        Gate gate = availableGate.get();
        
        parkingSession.setEntryGate(gate);

        // Inicia o tempo de processamento da cancela.
        entryMovementManager.startGateCrossing(
                parkingSession,
                gate,
                simulationClock.getCurrentTime()
        );

        entryGateQueueService.enqueue(parkingSession);

        // Inicia o cooldown da cancela utilizada.
        entryFlowManager.startCooldown(availableGate.get());
    }

    // Processa os veículos que terminaram de atravessar a cancela.
    private void processGateCrossing() {

        for (ParkingSession parkingSession : entryGateQueueService.getWaitingSessions()) {

            if (!entryMovementManager.hasFinishedCrossing(
                    parkingSession,
                    simulationClock.getCurrentTime())) {
                continue;
            }

            // Inicia o deslocamento até a vaga.
            parkingMovementManager.startParkingSearch(
                    parkingSession,
                    simulationClock.getCurrentTime()
            );

            parkingQueueService.enqueue(parkingSession);

            // Finaliza o processamento da cancela.
            entryMovementManager.finishGateCrossing(
                    parkingSession,
                    parkingSession.getEntryGate()
            );

            entryGateQueueService.remove(parkingSession);
        }
    }

}
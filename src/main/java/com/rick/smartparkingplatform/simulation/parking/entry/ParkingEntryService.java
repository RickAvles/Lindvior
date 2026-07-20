package com.rick.smartparkingplatform.simulation.parking.entry;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import com.rick.smartparkingplatform.simulation.queue.EntryQueueService;
import com.rick.smartparkingplatform.simulation.queue.ParkingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ParkingEntryService {

    // Arrival
    private final ArrivalManager arrivalManager;
    private final VehicleProvider vehicleProvider;
    // Gate
    private final EntryQueueService entryQueueService;
    private final EntryFlowManager entryFlowManager;
    private final ParkingQueueService parkingQueueService;
    // Domain
    private final ParkingSpotService parkingSpotService;
    private final ParkingSessionService parkingSessionService;

    @Transactional
    public void process() {

        processArrival();

        processGate();
    }

    private void processArrival() {

        if (!arrivalManager.shouldGenerateVehicle()) {
            return;
        }

        Vehicle vehicle = vehicleProvider.nextVehicle();

        entryQueueService.enqueue(vehicle);
    }

    private void processGate() {

        if (!entryFlowManager.canProcessNextVehicle()) {
            return;
        }

        if (!parkingSpotService.hasAvailableSpot()) {
            return;
        }

        if (!entryQueueService.hasWaitingVehicles()) {
            return;
        }

        Vehicle vehicle = entryQueueService.dequeue();

        parkingSessionService.validateNoOpenSession(vehicle);

        ParkingSpot parkingSpot = parkingSpotService.reserveAvailableSpot();

        ParkingSession parkingSession = parkingSessionService.startEntering(vehicle, parkingSpot);

        parkingQueueService.enqueue(parkingSession);

        entryFlowManager.startCooldown();
    }

}
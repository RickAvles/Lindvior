package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.ParkingSpot;
import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.simulation.service.EnteringQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParkingEntryService {

    private final VehicleService vehicleService;
    private final ParkingSpotService parkingSpotService;
    private final ParkingSessionService parkingSessionService;
    private final EnteringQueueService enteringQueueService;

    /**
     * Processa a entrada de um veículo no estacionamento.
     */
    @Transactional
    public void processEntry(String licensePlate) {

        Vehicle vehicle = vehicleService.findByLicensePlate(licensePlate);

        parkingSessionService.validateNoOpenSession(vehicle);

        ParkingSpot parkingSpot = parkingSpotService.findAvailableSpot();

        parkingSpotService.reserve(parkingSpot);

        ParkingSession parkingSession =
                parkingSessionService.createSession(
                        vehicle,
                        parkingSpot
                );

        enteringQueueService.enqueue(parkingSession);
    }

}
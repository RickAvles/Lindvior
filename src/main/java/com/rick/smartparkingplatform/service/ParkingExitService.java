package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.simulation.service.ExitQueueService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ParkingExitService {

    private final ParkingSpotService parkingSpotService;
    private final ParkingSessionService parkingSessionService;
    private final ExitQueueService exitQueueService;

    /**
     * Processa a saída de um veículo do estacionamento.
     */
    @Transactional
    public void processExit(ParkingSession parkingSession) {

        parkingSessionService.validateOpenSession(parkingSession);

        parkingSpotService.release(
                parkingSession.getParkingSpot()
        );

        parkingSessionService.startExit(parkingSession);

        exitQueueService.enqueue(parkingSession);
    }

}
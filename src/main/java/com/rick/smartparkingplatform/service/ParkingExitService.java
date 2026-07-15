package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.entity.ParkingSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParkingExitService {

    private final ParkingSpotService parkingSpotService;
    private final ParkingSessionService parkingSessionService;

    /**
     * Processa a saída de um veículo do estacionamento.
     */
    @Transactional
    public void processExit(UUID parkingSessionId) {

        ParkingSession parkingSession =
                parkingSessionService.getEntity(parkingSessionId);

        parkingSessionService.validateOpenSession(parkingSession);

        parkingSpotService.release(
                parkingSession.getParkingSpot()
        );

        parkingSessionService.closeSession(parkingSession);
    }

}

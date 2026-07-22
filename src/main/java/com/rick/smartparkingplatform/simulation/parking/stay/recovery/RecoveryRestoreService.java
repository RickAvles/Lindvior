package com.rick.smartparkingplatform.simulation.parking.stay.recovery;

import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import com.rick.smartparkingplatform.simulation.parking.flow.ParkingMovementManager;
import com.rick.smartparkingplatform.simulation.queue.ParkingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecoveryRestoreService {

    private final ParkingSessionService parkingSessionService;
    private final ParkingQueueService parkingQueueService;
    private final ParkingMovementManager parkingMovementManager;
    private final SimulationClock simulationClock;

    // Restaura todas as sessões pendentes após a reinicialização da aplicação.
    public void restoreSessions() {

        restoreEnteringSessions();
        restoreExitingSessions();
    }

    // Restaura as sessões que estavam entrando.
    private void restoreEnteringSessions() {

        LocalDateTime currentTime = simulationClock.getCurrentTime();

        parkingSessionService.getEnteringSessions().forEach(parkingSession -> {

            parkingMovementManager.startParkingSearch(
                    parkingSession,
                    currentTime
            );

            parkingQueueService.enqueue(parkingSession);
        });
    }

    // Restaura as sessões que estavam saindo.
    private void restoreExitingSessions() {

        parkingSessionService.getExitingSessions().forEach(parkingSessionService::restoreActive);
    }

}
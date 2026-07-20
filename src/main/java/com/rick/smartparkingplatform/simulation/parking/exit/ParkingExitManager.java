package com.rick.smartparkingplatform.simulation.parking.exit;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import com.rick.smartparkingplatform.simulation.queue.ExitQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingExitManager {

    @Value("${simulation.exit.cooldown-seconds}")
    private long cooldownSeconds;

    private LocalDateTime nextAvailableExitTime = LocalDateTime.MIN;

    private final ParkingSpotService parkingSpotService;
    private final ParkingSessionService parkingSessionService;
    private final ExitQueueService exitQueueService;

    // Inicia o processo de saída do veículo.
    public void startExit(ParkingSession parkingSession) {

        parkingSessionService.validateActiveSession(parkingSession);

        parkingSessionService.startExit(parkingSession);

        exitQueueService.enqueue(parkingSession);
    }

    // Decide se uma saída pode ser processada.
    public boolean canProcessExit(LocalDateTime currentTime) {

        return exitQueueService.hasWaitingSessions()
                && !currentTime.isBefore(nextAvailableExitTime);
    }

    // Finaliza a saída do veículo.
    public void processExit(ParkingSession parkingSession, LocalDateTime currentTime) {

        parkingSpotService.release(parkingSession.getParkingSpot());

        parkingSessionService.closeSession(parkingSession, currentTime);

        startCooldown(currentTime);
    }

    // Inicia um novo período de cooldown da saída.
    private void startCooldown(LocalDateTime currentTime) {

        nextAvailableExitTime = currentTime.plusSeconds(cooldownSeconds);
    }

}

package com.rick.smartparkingplatform.simulation.parking.flow;

import com.rick.smartparkingplatform.simulation.queue.ParkingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingMovementManager {

    @Value("${simulation.parking.cooldown-seconds}")
    private long cooldownSeconds;

    private final ParkingQueueService parkingQueueService;

    private LocalDateTime nextAvailableParkingTime;

    // Decide se um veículo pode finalizar o estacionamento.
    public boolean isParkingBlocked(LocalDateTime currentTime) {

        return !parkingQueueService.hasWaitingSessions()
                || !isParkingAvailable(currentTime);
    }

    // Registra um estacionamento e define o próximo instante disponível.
    public void registerParking(LocalDateTime currentTime) {

        nextAvailableParkingTime =
                currentTime.plusSeconds(cooldownSeconds);
    }

    // Verifica se o fluxo de estacionamento está disponível.
    private boolean isParkingAvailable(LocalDateTime currentTime) {

        if (nextAvailableParkingTime == null) {
            return true;
        }

        return !currentTime.isBefore(nextAvailableParkingTime);
    }

}
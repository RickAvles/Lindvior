package com.rick.smartparkingplatform.simulation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingArrivalManager {

    @Value("${simulation.parking.cooldown-seconds}")
    private long cooldownSeconds;

    private LocalDateTime nextAvailableParkingTime;

    /**
     * Verifica se um veículo pode finalizar
     * o estacionamento neste instante.
     */
    public boolean canPark(LocalDateTime currentTime) {

        if (nextAvailableParkingTime == null) {
            return true;
        }

        return !currentTime.isBefore(
                nextAvailableParkingTime
        );
    }

    /**
     * Registra um estacionamento e define
     * o próximo instante disponível.
     */
    public void registerParking(LocalDateTime currentTime) {

        nextAvailableParkingTime =
                currentTime.plusSeconds(
                        cooldownSeconds
                );
    }

}
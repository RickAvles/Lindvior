package com.rick.smartparkingplatform.simulation.service;

import com.rick.smartparkingplatform.dto.response.ParkingResponse;
import com.rick.smartparkingplatform.service.ParkingService;
import com.rick.smartparkingplatform.simulation.clock.SimulationClock;
import com.rick.smartparkingplatform.simulation.enums.TrafficIntensity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class TrafficProfileService {

    private final SimulationClock simulationClock;
    private final ParkingService parkingService;

    /**
     * Retorna a intensidade operacional correspondente ao momento atual do expediente.
     */
    public TrafficIntensity getCurrentIntensity() {

        double progress = calculateOperatingProgress();

        if (progress < 0.20) {
            return TrafficIntensity.LOW;
        }

        if (progress < 0.40) {
            return TrafficIntensity.MEDIUM;
        }

        if (progress < 0.60) {
            return TrafficIntensity.HIGH;
        }

        if (progress < 0.80) {
            return TrafficIntensity.MEDIUM;
        }

        if (progress < 0.95) {
            return TrafficIntensity.HIGH;
        }

        return TrafficIntensity.LOW;
    }

    /**
     * Calcula o percentual de progresso do expediente atual.
     */
    private double calculateOperatingProgress() {

        ParkingResponse parking = parkingService.getParking();

        LocalTime openingTime = parking.openingTime();

        LocalTime closingTime = parking.closingTime();

        LocalTime currentTime = simulationClock.getCurrentTime().toLocalTime();

        long operatingMinutes = Duration.between(openingTime, closingTime).toMinutes();

        long elapsedMinutes = Duration.between(openingTime, currentTime).toMinutes();

        double progress =
                (double) elapsedMinutes / operatingMinutes;

        return Math.clamp(progress, 0.0, 1.0);
    }

    /**
     * Retorna a probabilidade base de geração de entradas
     * para a intensidade operacional atual.
     */
    public double getEntryProbability() {

        return switch (getCurrentIntensity()) {

            case LOW -> 0.15;

            case MEDIUM -> 0.40;

            case HIGH -> 0.70;
        };
    }

}

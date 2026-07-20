package com.rick.smartparkingplatform.simulation.service;

import com.rick.smartparkingplatform.dto.response.ParkingResponse;
import com.rick.smartparkingplatform.service.ParkingService;
import com.rick.smartparkingplatform.simulation.conditions.ConditionService;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class TrafficProfileService {

    private final SimulationClock simulationClock;
    private final ParkingService parkingService;
    private final ConditionService conditionService;

    // Retorna a intensidade operacional correspondente ao momento atual do expediente.
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

    // Retorna a probabilidade final de geração de entradas.
    public double getEntryProbability() {

        double probability = getBaseProbability();

        probability = applyDayModifier(probability);
        probability = applyWeatherModifier(probability);

        return Math.clamp(probability, 0.0, 1.0);
    }

    // Calcula o percentual de progresso do expediente atual.
    private double calculateOperatingProgress() {

        ParkingResponse parking = parkingService.getParking();

        LocalTime openingTime = parking.openingTime();
        LocalTime closingTime = parking.closingTime();
        LocalTime currentTime = simulationClock.getCurrentTime().toLocalTime();

        long operatingMinutes =
                Duration.between(openingTime, closingTime).toMinutes();

        long elapsedMinutes =
                Duration.between(openingTime, currentTime).toMinutes();

        double progress =
                (double) elapsedMinutes / operatingMinutes;

        return Math.clamp(progress, 0.0, 1.0);
    }

    // Retorna a demanda base conforme a curva horária.
    private double getBaseProbability() {

        return switch (getCurrentIntensity()) {

            case LOW -> 0.15;
            case MEDIUM -> 0.40;
            case HIGH -> 0.70;
        };
    }

    // Aplica o impacto do tipo do dia na demanda.
    private double applyDayModifier(double probability) {

        return switch (conditionService.getCurrentDayType()) {

            case WEEKDAY -> probability;
            case SATURDAY -> probability * 1.25;
            case SUNDAY -> probability * 1.15;
        };
    }

    // Aplica o impacto do clima na demanda.
    private double applyWeatherModifier(double probability) {

        return switch (conditionService.getCurrentWeather()) {

            case SUNNY -> probability * 0.90;
            case CLOUDY -> probability;
            case RAINY -> probability * 1.20;
        };
    }

}
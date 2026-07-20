package com.rick.smartparkingplatform.simulation.parking.stay;

import com.rick.smartparkingplatform.entity.ParkingSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ParkingStayManager {

    private final StayProbabilityCalculator stayProbabilityCalculator;

    // Avalia se a sessão deverá ser encerrada neste ciclo.
    public StayDecision evaluate(ParkingSession parkingSession, LocalDateTime currentTime) {

        double probability =
                stayProbabilityCalculator.calculateExitProbability(
                        parkingSession,
                        currentTime
                );

        boolean shouldExit =
                ThreadLocalRandom.current().nextDouble() <= probability;

        return new StayDecision(
                shouldExit,
                probability
        );
    }

}
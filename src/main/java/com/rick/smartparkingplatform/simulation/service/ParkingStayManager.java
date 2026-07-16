package com.rick.smartparkingplatform.simulation.service;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.simulation.generator.ParkingStayGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class ParkingStayManager {

    private final ParkingStayGenerator parkingStayGenerator;
    private final Random random = new Random();

    /**
     * Avalia probabilisticamente se a sessão
     * deverá ser encerrada neste ciclo.
     */
    public boolean shouldExit(
            ParkingSession parkingSession,
            LocalDateTime currentTime) {

        double probability =
                parkingStayGenerator.calculateExitProbability(
                        parkingSession,
                        currentTime
                );

        return random.nextDouble() <= probability;
    }

}
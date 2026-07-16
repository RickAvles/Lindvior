package com.rick.smartparkingplatform.simulation.generator;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.simulation.enums.StayCurve;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import com.rick.smartparkingplatform.simulation.service.RecoveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ParkingStayGenerator {

    private final RecoveryService recoveryService;
    private final SimulationLogger simulationLogger;

    /**
     * Calcula a probabilidade de saída
     * da sessão no ciclo atual.
     */
    public double calculateExitProbability(
            ParkingSession parkingSession,
            LocalDateTime currentTime) {

        if (recoveryService.shouldUseRecoveryCurve(
                parkingSession,
                currentTime)) {

            long recoveryElapsedSeconds =
                    recoveryService.getRecoveryElapsedSeconds(
                            currentTime
                    );

            double probability =
                    StayCurve.RECOVERY.getProbability(
                            recoveryElapsedSeconds
                    );

            if (recoveryService.shouldLogRecovery(
                    recoveryElapsedSeconds)) {

                simulationLogger.recovery(
                        parkingSession
                                .getVehicle()
                                .getLicensePlate(),
                        recoveryElapsedSeconds,
                        probability
                );
            }

            return probability;
        }

        long elapsedMinutes =
                calculateElapsedMinutes(
                        parkingSession,
                        currentTime
                );

        StayCurve stayCurve =
                parkingSession
                        .getVehicle()
                        .getStayProfile()
                        .getStayCurve();

        return stayCurve.getProbability(
                elapsedMinutes
        );
    }

    /**
     * Calcula o tempo de permanência
     * da sessão em minutos.
     */
    private long calculateElapsedMinutes(
            ParkingSession parkingSession,
            LocalDateTime currentTime) {

        return Duration
                .between(
                        parkingSession.getEntryTime(),
                        currentTime
                )
                .toMinutes();
    }

}
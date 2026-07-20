package com.rick.smartparkingplatform.simulation.parking.stay.recovery;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import com.rick.smartparkingplatform.simulation.parking.stay.StayCurve;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecoveryManager {

    private static final long RECOVERY_DURATION_MINUTES = 5;

    private LocalDateTime recoveryStartedAt;
    private LocalDateTime recoveryEndsAt;

    private final SimulationClock simulationClock;
    private final SimulationLogger simulationLogger;

    // Inicializa o período de recuperação.
    public void startRecovery() {

        LocalDateTime currentTime = simulationClock.getCurrentTime();

        recoveryStartedAt = currentTime;
        recoveryEndsAt = currentTime.plusMinutes(RECOVERY_DURATION_MINUTES);

        simulationLogger.startup(recoveryStartedAt, RECOVERY_DURATION_MINUTES);
    }

    // Retorna a curva de recuperação quando aplicável.
    public StayCurve getRecoveryCurve(ParkingSession parkingSession, LocalDateTime currentTime) {

        if (!isRecoveryPeriod(currentTime)) {
            return null;
        }

        if (!enteredBeforeRecovery(parkingSession) && !isOvernight(parkingSession, currentTime)) {
            return null;
        }

        return StayCurve.RECOVERY;
    }

    // Retorna os segundos decorridos desde o início da recuperação.
    public long getRecoveryElapsedSeconds(LocalDateTime currentTime) {

        if (recoveryStartedAt == null) {
            return 0;
        }

        return Duration.between(recoveryStartedAt, currentTime).toSeconds();
    }

    // Decide se a recuperação deve ser registrada.
    public boolean shouldLogRecovery(long elapsedSeconds) {

        return StayCurve.RECOVERY.shouldLog(elapsedSeconds);
    }

    // Verifica se o período de recuperação ainda está ativo.
    private boolean isRecoveryPeriod(LocalDateTime currentTime) {

        return recoveryStartedAt != null
                && currentTime.isBefore(recoveryEndsAt);
    }

    // Verifica se a sessão foi iniciada antes do início da recuperação.
    private boolean enteredBeforeRecovery(ParkingSession parkingSession) {

        return parkingSession.getEntryTime().isBefore(recoveryStartedAt);
    }

    // Verifica se a sessão foi iniciada em um dia anterior ao atual.
    private boolean isOvernight(ParkingSession parkingSession, LocalDateTime currentTime) {

        return parkingSession.getEntryTime().toLocalDate().isBefore(currentTime.toLocalDate());
    }

}
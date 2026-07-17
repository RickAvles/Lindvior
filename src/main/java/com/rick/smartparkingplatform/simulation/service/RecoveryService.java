package com.rick.smartparkingplatform.simulation.service;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.simulation.clock.SimulationClock;
import com.rick.smartparkingplatform.simulation.enums.StayCurve;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecoveryService {

    private static final long RECOVERY_DURATION_MINUTES = 5;

    private LocalDateTime startupTime;

    private LocalDateTime recoveryEndsAt;

    private final SimulationClock simulationClock;

    private final SimulationLogger simulationLogger;

    private LocalDateTime recoveryStartedAt;

    private final ParkingSessionService parkingSessionService;

    private final EnteringQueueService enteringQueueService;

    /**
     * Inicializa o período de recuperação
     * após a partida da aplicação.
     */
    @PostConstruct
    public void startRecovery() {

        recoveryStartedAt = simulationClock.getCurrentTime();

        simulationLogger.startup(recoveryStartedAt, RECOVERY_DURATION_MINUTES);

        startupTime = simulationClock.getCurrentTime();

        recoveryEndsAt =
                startupTime.plusMinutes(RECOVERY_DURATION_MINUTES);

        restoreEnteringSessions();
    }

    /**
     * Verifica se a sessão foi criada antes
     * da inicialização da recuperação.
     */
    private boolean startedBeforeRecovery(
            ParkingSession parkingSession) {

        return parkingSession
                .getEntryTime()
                .isBefore(recoveryStartedAt);
    }

    /**
     * Determina se a sessão deve utilizar
     * a curva especial de recuperação.
     */
    public boolean shouldUseRecoveryCurve(
            ParkingSession parkingSession,
            LocalDateTime currentTime) {

        if (!isRecoveryPeriod(currentTime)) {
            return false;
        }

        return startedBeforeRecovery(parkingSession)
                || isOvernight(parkingSession, currentTime);
    }

    /**
     * Verifica se a simulação ainda está
     * no período de recuperação.
     */
    public boolean isRecoveryPeriod(
            LocalDateTime currentTime) {

        if (startupTime == null) {
            return false;
        }

        return currentTime.isBefore(recoveryEndsAt);
    }

    /**
     * Retorna o tempo decorrido desde o
     * início da recuperação em segundos.
     *
     * @param currentTime horário atual da simulação.
     * @return segundos decorridos desde o início da recuperação.
     */
    public long getRecoveryElapsedSeconds(
            LocalDateTime currentTime) {

        if (startupTime == null) {
            return 0;
        }

        return Duration
                .between(
                        startupTime,
                        currentTime
                )
                .toSeconds();
    }

    /**
     * Verifica se a sessão pertence
     * a um dia anterior.
     */
    private boolean isOvernight(
            ParkingSession parkingSession,
            LocalDateTime currentTime) {

        return parkingSession
                .getEntryTime()
                .toLocalDate()
                .isBefore(
                        currentTime.toLocalDate()
                );
    }

    /**
     * Determina se a recuperação deve
     * ser registrada no log.
     *
     * @param elapsedSeconds segundos decorridos desde
     *                       o início da recuperação.
     * @return true quando houve mudança de faixa.
     */
    public boolean shouldLogRecovery(
            long elapsedSeconds) {

        for (long limit : StayCurve.RECOVERY.getLimits()) {

            if (elapsedSeconds == limit) {
                return true;
            }
        }

        return false;
    }

    private void restoreEnteringSessions() {

        List<ParkingSession> enteringSessions =
                parkingSessionService.getEnteringSessions();

        enteringSessions.forEach(enteringQueueService::enqueue);
    }

}
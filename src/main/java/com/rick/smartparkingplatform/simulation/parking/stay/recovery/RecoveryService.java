package com.rick.smartparkingplatform.simulation.parking.stay.recovery;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.simulation.parking.stay.StayCurve;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RecoveryService {

    private final RecoveryManager recoveryManager;
    private final RecoveryRestoreService recoveryRestoreService;


    // Inicializa o período de recuperação e restaura as sessões pendentes.
    @PostConstruct
    public void startRecovery() {

        recoveryManager.startRecovery();
        recoveryRestoreService.restoreSessions();
    }

    // Retorna a curva de recuperação quando aplicável.
    public StayCurve getRecoveryCurve(ParkingSession parkingSession, LocalDateTime currentTime) {

        return recoveryManager.getRecoveryCurve(parkingSession, currentTime);
    }

    // Retorna os segundos decorridos desde a recuperação.
    public long getRecoveryElapsedSeconds(LocalDateTime currentTime) {

        return recoveryManager.getRecoveryElapsedSeconds(currentTime);
    }

    // Decide se a recuperação deve ser registrada.
    public boolean shouldLogRecovery(long elapsedSeconds) {

        return recoveryManager.shouldLogRecovery(elapsedSeconds);
    }

}
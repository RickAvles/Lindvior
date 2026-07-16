package com.rick.smartparkingplatform.simulation.log;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.simulation.enums.StayCurve;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class SimulationLogger {

    public void entry(String plate) {

        log.info("[ENTRY] Veículo {} entrou.", plate);
    }

    public void exit(String plate, double probability) {

        log.info(
                "[EXIT] Veículo {} saiu. Probabilidade={}%",
                plate,
                String.format("%.2f", probability * 100)
        );
    }

    public void recovery(String plate, long recoveryElapsedSeconds, double probability) {

        log.info(
                "[RECOVERY] Veículo {} | segundo={} | probabilidade={}",
                plate,
                recoveryElapsedSeconds,
                String.format("%.2f", probability * 100)
        );
    }

    public void startup(LocalDateTime startedAt, long durationMinutes) {
        log.info(
                "[RECOVERY] Período de recuperação iniciado. Início={} Duração={} minutos.",
                startedAt,
                durationMinutes
        );
    }

    public void decision(
            String plate,
            StayCurve stayCurve,
            long elapsedMinutes,
            double probability) {

//        log.info(
//                "[DECISION] placa={} curva={} tempo={}min probabilidade={}%",
//                plate,
//                stayCurve.name(),
//                elapsedMinutes,
//                String.format("%.2f", probability * 100)
//        );
    }

}
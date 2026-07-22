package com.rick.smartparkingplatform.simulation.gate;

import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.service.ParkingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class EntryMovementManager {

    // Controla o término do processamento de cada veículo na cancela.
    private final Map<UUID, LocalDateTime> gateProcessingTimes = new ConcurrentHashMap<>();

    // Serviço responsável pelas configurações do estacionamento.
    private final ParkingService parkingService;

    // Inicia o processamento da cancela para um veículo.
    public void startGateCrossing(ParkingSession parkingSession, Gate gate, LocalDateTime currentTime) {

        gate.setCurrentSession(parkingSession);

        Parking parking = parkingService.getCurrentParking();

        int crossingTime = ThreadLocalRandom.current().nextInt(
                parking.getEntryGateMinProcessingSeconds(),
                parking.getEntryGateMaxProcessingSeconds() + 1
        );

        gateProcessingTimes.put(
                parkingSession.getId(),
                currentTime.plusSeconds(crossingTime)
        );
    }

    // Verifica se o veículo terminou o processamento da cancela.
    public boolean hasFinishedCrossing(ParkingSession parkingSession,
                                       LocalDateTime currentTime) {

        LocalDateTime finishTime = gateProcessingTimes.get(parkingSession.getId());

        return finishTime != null
                && !currentTime.isBefore(finishTime);
    }

    // Remove o controle do processamento da cancela.
    public void finishGateCrossing(ParkingSession parkingSession, Gate gate) {

        gate.setCurrentSession(null);

        gateProcessingTimes.remove(parkingSession.getId());
    }

}

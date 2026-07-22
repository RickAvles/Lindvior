package com.rick.smartparkingplatform.simulation.parking.flow;

import com.rick.smartparkingplatform.entity.ParkingSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class ParkingMovementManager {

    private final Map<UUID, LocalDateTime> parkingSearches = new ConcurrentHashMap<>();

    // Inicia a procura por vaga.
    public void startParkingSearch(
            ParkingSession parkingSession,
            LocalDateTime currentTime) {

        // Sorteia o tempo base da procura.
        int baseTime = ThreadLocalRandom.current().nextInt(10, 21);

        // Ajusta o tempo conforme o andar.
        int floorMultiplier = Math.abs(
                parkingSession.getParkingSpot()
                        .getParkingSector()
                        .getFloor()
        ) + 1;

        LocalDateTime finishTime = currentTime.plusSeconds(
                (long) baseTime * floorMultiplier
        );

        parkingSearches.put(parkingSession.getId(), finishTime);
    }

    // Verifica se a sessão concluiu a procura por vaga.
    public boolean hasFinishedSearching(
            ParkingSession parkingSession,
            LocalDateTime currentTime) {

        LocalDateTime finishTime = parkingSearches.get(
                parkingSession.getId()
        );

        return finishTime != null
                && !currentTime.isBefore(finishTime);
    }

    // Finaliza o controle da procura por vaga.
    public void finishParkingSearch(ParkingSession parkingSession) {

        parkingSearches.remove(parkingSession.getId());
    }

}
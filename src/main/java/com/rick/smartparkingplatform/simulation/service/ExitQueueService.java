package com.rick.smartparkingplatform.simulation.service;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
public class ExitQueueService {

    private final Queue<ParkingSession> queue = new ConcurrentLinkedQueue<>();

    private final SimulationLogger simulationLogger;

    /**
     * Adiciona uma sessão na fila de saída.
     */
    public void enqueue(ParkingSession parkingSession) {

        queue.offer(parkingSession);

        simulationLogger.exitQueue(
                parkingSession.getVehicle().getLicensePlate(),
                queue.size()
        );
    }

    /**
     * Remove a próxima sessão da fila de saída.
     */
    public ParkingSession dequeue() {

        ParkingSession parkingSession = queue.poll();

        if (parkingSession != null) {
            simulationLogger.exitQueueRelease(
                    parkingSession.getVehicle().getLicensePlate(),
                    queue.size()
            );
        }

        return parkingSession;
    }

    /**
     * Verifica se existem veículos aguardando saída.
     */
    public boolean hasVehicles() {
        return !queue.isEmpty();
    }

    /**
     * Retorna a quantidade de veículos aguardando saída.
     */
    public int size() {
        return queue.size();
    }

}
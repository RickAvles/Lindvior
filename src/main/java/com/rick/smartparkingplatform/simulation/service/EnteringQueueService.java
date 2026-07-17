package com.rick.smartparkingplatform.simulation.service;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class EnteringQueueService {

    private final Queue<ParkingSession> queue =
            new ConcurrentLinkedQueue<>();

    private final SimulationLogger simulationLogger;

    public EnteringQueueService(
            SimulationLogger simulationLogger
    ) {
        this.simulationLogger = simulationLogger;
    }

    /**
     * Adiciona uma sessão à fila interna
     * de veículos procurando vaga.
     */
    public void enqueue(ParkingSession parkingSession) {

        queue.offer(parkingSession);

        simulationLogger.enteringQueue(
                parkingSession.getVehicle().getLicensePlate(),
                queue.size()
        );
    }

    /**
     * Remove o próximo veículo da fila.
     */
    public ParkingSession dequeue() {

        ParkingSession parkingSession = queue.poll();

        if (parkingSession != null) {

            simulationLogger.enteringQueueRelease(
                    parkingSession.getVehicle().getLicensePlate(),
                    queue.size()
            );
        }

        return parkingSession;
    }

    /**
     * Verifica se existem veículos
     * aguardando estacionamento.
     */
    public boolean hasVehicles() {

        return !queue.isEmpty();
    }

    /**
     * Quantidade atual de veículos na fila.
     */
    public int size() {

        return queue.size();
    }

}
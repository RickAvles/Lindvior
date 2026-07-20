package com.rick.smartparkingplatform.simulation.queue;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
public class ParkingQueueService {

    private final SimulationLogger simulationLogger;

    private final Queue<ParkingSession> queue = new ConcurrentLinkedQueue<>();

    // Adiciona uma sessão ao final da fila.
    public void enqueue(ParkingSession session) {

        queue.offer(session);

        simulationLogger.enteringQueue(session.getVehicle().getLicensePlate(), queue.size());
    }

    // Remove a próxima sessão da fila.
    public ParkingSession dequeue() {

        ParkingSession session = queue.poll();

        if (session != null) {
            simulationLogger.enteringQueueRelease(session.getVehicle().getLicensePlate(), queue.size());
        }

        return session;
    }

    // Verifica se existem sessões aguardando estacionamento.
    public boolean hasWaitingSessions() {
        return !queue.isEmpty();
    }

    // Retorna a quantidade de sessões na fila.
    public int size() {
        return queue.size();
    }

}
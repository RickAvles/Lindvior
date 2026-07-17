package com.rick.smartparkingplatform.simulation.service;

import com.rick.smartparkingplatform.entity.Vehicle;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
@RequiredArgsConstructor
public class EntryQueueService {

    private final SimulationLogger simulationLogger;

    private final Queue<Vehicle> queue = new ConcurrentLinkedQueue<>();

    /**
     * Adiciona um veículo ao final da fila.
     */
    public void enqueue(Vehicle vehicle) {

        queue.offer(vehicle);

        simulationLogger.entryQueue(
                vehicle.getLicensePlate(),
                queue.size()
        );
    }

    /**
     * Remove o próximo veículo da fila.
     *
     * @return próximo veículo da fila ou null quando vazia.
     */
    public Vehicle dequeue() {

        Vehicle vehicle = queue.poll();

        if (vehicle != null) {

            simulationLogger.entryQueueRelease(
                    vehicle.getLicensePlate(),
                    queue.size()
            );
        }

        return vehicle;
    }

    /**
     * Verifica se existem veículos aguardando.
     */
    public boolean hasVehicles() {

        return !queue.isEmpty();
    }

    /**
     * Retorna a quantidade de veículos na fila.
     */
    public int size() {

        return queue.size();
    }

}
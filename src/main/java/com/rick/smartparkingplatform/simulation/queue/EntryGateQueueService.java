package com.rick.smartparkingplatform.simulation.queue;

import com.rick.smartparkingplatform.entity.ParkingSession;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class EntryGateQueueService {

    // Armazena as sessões que estão atravessando a cancela de entrada.
    private final Queue<ParkingSession> queue = new ConcurrentLinkedQueue<>();

    // Adiciona uma sessão à fila da cancela de entrada.
    public void enqueue(ParkingSession parkingSession) {

        queue.offer(parkingSession);
    }


    // Remove uma sessão específica da fila da cancela de entrada.
    public void remove(ParkingSession parkingSession) {

        queue.remove(parkingSession);
    }

    // Retorna todas as sessões da fila da cancela de entrada.
    public Collection<ParkingSession> getWaitingSessions() {

        return List.copyOf(queue);
    }

    // Retorna a quantidade de sessões na fila da cancela de entrada.
    public int size() {

        return queue.size();
    }

}

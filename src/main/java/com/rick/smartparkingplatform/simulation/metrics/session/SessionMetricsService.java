package com.rick.smartparkingplatform.simulation.metrics.session;

import com.rick.smartparkingplatform.entity.ParkingSession;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionMetricsService {

    private final Map<UUID, SessionMetrics> sessions =
            new ConcurrentHashMap<>();

    // Inicializa as métricas temporárias de uma nova sessão.
    public void startSession(
            ParkingSession parkingSession,
            LocalDateTime currentTime) {

        SessionMetrics metrics = new SessionMetrics();

        metrics.setEntryQueueAt(currentTime);

        sessions.put(parkingSession.getId(), metrics);
    }

    // Inicializa as métricas de uma sessão recuperada.
    public void startRecoveredSession(ParkingSession parkingSession) {

        SessionMetrics metrics = new SessionMetrics();

        sessions.put(parkingSession.getId(), metrics);
    }

    // Retorna as métricas da sessão.
    public SessionMetrics get(ParkingSession parkingSession) {

        return sessions.get(parkingSession.getId());
    }

    // Remove as métricas da sessão após sua finalização.
    public void remove(ParkingSession parkingSession) {

        sessions.remove(parkingSession.getId());
    }

    // Remove todas as métricas da simulação.
    public void clear() {

        sessions.clear();
    }

}
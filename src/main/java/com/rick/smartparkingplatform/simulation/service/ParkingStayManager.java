package com.rick.smartparkingplatform.simulation.service;

import com.rick.smartparkingplatform.entity.ParkingSession;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ParkingStayManager {

    private final Map<UUID, LocalDateTime> scheduledExits = new ConcurrentHashMap<>();

    /**
     * Agenda o horário previsto de saída de uma sessão.
     */
    public void scheduleExit(
            ParkingSession parkingSession,
            Duration stayDuration) {

        LocalDateTime scheduledExit =
                parkingSession.getEntryTime().plus(stayDuration);

        scheduledExits.put(
                parkingSession.getId(),
                scheduledExit
        );
    }

    /**
     * Verifica se uma sessão atingiu o horário previsto de saída.
     */
    public boolean shouldExit(
            ParkingSession parkingSession,
            LocalDateTime currentTime) {

        LocalDateTime scheduledExit =
                scheduledExits.get(parkingSession.getId());

        if (scheduledExit == null) {
            return false;
        }

        return !currentTime.isBefore(scheduledExit);
    }

    /**
     * Remove o agendamento de saída de uma sessão.
     */
    public void removeSchedule(ParkingSession parkingSession) {

        scheduledExits.remove(parkingSession.getId());
    }


}
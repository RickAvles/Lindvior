package com.rick.smartparkingplatform.simulation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EntryFlowManager {

    @Value("${simulation.entry.cooldown-seconds}")
    private long cooldownSeconds;

    private LocalDateTime nextAvailableEntryTime;

    /**
     * Verifica se uma nova entrada
     * pode ser processada.
     */
    public boolean canProcessEntry(
            LocalDateTime currentTime) {

        if (nextAvailableEntryTime == null) {
            return true;
        }

        return !currentTime.isBefore(
                nextAvailableEntryTime
        );
    }

    /**
     * Registra uma entrada e define
     * o próximo instante disponível.
     */
    public void registerEntry(
            LocalDateTime currentTime) {

        nextAvailableEntryTime =
                currentTime.plusSeconds(
                        cooldownSeconds
                );
    }

}
package com.rick.smartparkingplatform.simulation.engine;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SimulationClock {

    // Retorna o horário atual utilizado pela simulação.
    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

}
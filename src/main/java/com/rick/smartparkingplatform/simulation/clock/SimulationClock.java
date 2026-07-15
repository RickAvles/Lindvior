package com.rick.smartparkingplatform.simulation.clock;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SimulationClock {

    public LocalDateTime getCurrentTime() {
        return LocalDateTime.now();
    }

}

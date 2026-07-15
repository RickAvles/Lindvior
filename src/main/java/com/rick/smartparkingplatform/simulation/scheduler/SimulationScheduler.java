package com.rick.smartparkingplatform.simulation.scheduler;

import com.rick.smartparkingplatform.simulation.engine.SimulationEngine;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimulationScheduler {

    private final SimulationEngine simulationEngine;

    @Scheduled(fixedRate = 1000)
    public void tick() {
        simulationEngine.processTick();
    }

}

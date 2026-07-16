package com.rick.smartparkingplatform.simulation.engine;

import com.rick.smartparkingplatform.service.ParkingService;
import com.rick.smartparkingplatform.simulation.clock.SimulationClock;
import com.rick.smartparkingplatform.simulation.decision.DecisionEngine;
import com.rick.smartparkingplatform.simulation.enums.SimulationState;
import com.rick.smartparkingplatform.simulation.operation.OperatingHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SimulationEngine {

    private final DecisionEngine decisionEngine;
    private final SimulationClock simulationClock;
    private final OperatingHoursService operatingHoursService;
    private final ParkingService parkingService;

    /**
     * Executa um ciclo completo da simulação.
     */
    public void processTick() {

        if (!parkingService.exists()) {
            return;
        }

        LocalDateTime currentTime = simulationClock.getCurrentTime();

        SimulationState state = operatingHoursService.getCurrentState(currentTime);

        switch (state) {

            case OPEN -> decisionEngine.processOpenTick();

            case CLOSED -> decisionEngine.processClosedTick();
        }
    }

}
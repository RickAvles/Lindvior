package com.rick.smartparkingplatform.simulation.engine;

import com.rick.smartparkingplatform.service.ParkingService;
import com.rick.smartparkingplatform.simulation.conditions.ConditionService;
import com.rick.smartparkingplatform.simulation.operation.OperatingHoursService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SimulationEngine {

    private boolean initialized;

    private final ConditionService conditionService;

    private final DecisionEngine decisionEngine;
    private final SimulationClock simulationClock;
    private final OperatingHoursService operatingHoursService;
    private final ParkingService parkingService;

    public void processTick() {

        if (!parkingService.exists()) {
            return;
        }

        initialize();

        LocalDateTime currentTime = simulationClock.getCurrentTime();

        switch (operatingHoursService.getCurrentState(currentTime)) {

            case OPEN -> decisionEngine.processOpenTick();

            case CLOSED -> decisionEngine.processClosedTick();
        }
    }

    private void initialize() {

        if (initialized) {
            return;
        }

        conditionService.initialize();

        initialized = true;
    }

}
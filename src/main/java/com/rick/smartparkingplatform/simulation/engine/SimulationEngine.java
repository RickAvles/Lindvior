package com.rick.smartparkingplatform.simulation.engine;

import com.rick.smartparkingplatform.entity.Parking;
import com.rick.smartparkingplatform.notification.NotificationEvent;
import com.rick.smartparkingplatform.notification.NotificationSeverity;
import com.rick.smartparkingplatform.notification.NotificationType;
import com.rick.smartparkingplatform.notification.producer.NotificationEventProducer;
import com.rick.smartparkingplatform.service.ParkingService;
import com.rick.smartparkingplatform.simulation.conditions.ConditionService;
import com.rick.smartparkingplatform.simulation.gate.EntryGateManager;
import com.rick.smartparkingplatform.simulation.gate.ExitGateManager;
import com.rick.smartparkingplatform.simulation.metrics.dashboard.SimulationMetricsService;
import com.rick.smartparkingplatform.simulation.metrics.statistics.SimulationStatisticsService;
import com.rick.smartparkingplatform.simulation.operation.OperatingHoursService;
import com.rick.smartparkingplatform.simulation.operation.SimulationState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SimulationEngine {

    private boolean initialized;

    private final ConditionService conditionService;

    private final DecisionEngine decisionEngine;
    private final SimulationClock simulationClock;
    private final OperatingHoursService operatingHoursService;
    private final ParkingService parkingService;

    private final EntryGateManager entryGateManager;
    private final ExitGateManager exitGateManager;

    private final SimulationMetricsService simulationMetricsService;
    private final SimulationStatisticsService simulationStatisticsService;
    private final NotificationEventProducer notificationEventProducer;

    private SimulationState parkingPreviousState;


    public void processTick() {

        if (!parkingService.exists()) {
            return;
        }

        initialize();

        SimulationState parkingCurrentState = operatingHoursService.getCurrentState(simulationClock.getCurrentTime());

        if (parkingPreviousState == null) {
            parkingPreviousState = parkingCurrentState;
        }

        if (parkingCurrentState != parkingPreviousState) {
            if (parkingCurrentState == SimulationState.OPEN) {
                notificationEventProducer.send(
                        new NotificationEvent(
                                NotificationType.PARKING_OPEN,
                                NotificationSeverity.INFO,
                                "Parking operation started.",
                                simulationClock.getCurrentTime()
                        )
                );
            } else {
                notificationEventProducer.send(
                        new NotificationEvent(
                                NotificationType.PARKING_CLOSED,
                                NotificationSeverity.INFO,
                                "Parking operation ended.",
                                simulationClock.getCurrentTime()
                        )
                );
            }
        }

        switch (parkingCurrentState) {

            case OPEN -> decisionEngine.processOpenTick();

            case CLOSED -> decisionEngine.processClosedTick();
        }

        parkingPreviousState = parkingCurrentState;


        simulationMetricsService.update();
    }

    private void initialize() {

        if (initialized) {
            return;
        }

        Parking parking = parkingService.getCurrentParking();

        conditionService.initialize();

        entryGateManager.initialize(parking.getEntryGates());
        exitGateManager.initialize(parking.getExitGates());

        simulationStatisticsService.start(simulationClock.getCurrentTime());

        initialized = true;
    }

}
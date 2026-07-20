package com.rick.smartparkingplatform.simulation.engine;

import com.rick.smartparkingplatform.simulation.conditions.ConditionService;
import com.rick.smartparkingplatform.simulation.parking.entry.ParkingEntryService;
import com.rick.smartparkingplatform.simulation.parking.exit.ParkingExitService;
import com.rick.smartparkingplatform.simulation.parking.flow.ParkingFlowService;
import com.rick.smartparkingplatform.simulation.parking.stay.ParkingStayService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DecisionEngine {

    private final ConditionService conditionService;

    private final ParkingEntryService parkingEntryService;
    private final ParkingFlowService parkingFlowService;
    private final ParkingStayService parkingStayService;
    private final ParkingExitService parkingExitService;

    public void processOpenTick() {

        conditionService.update();

        parkingEntryService.process();

        parkingFlowService.process();

        parkingStayService.process();

        parkingExitService.process();
    }

    public void processClosedTick() {

        conditionService.update();

        parkingStayService.process();

        parkingExitService.process();
    }

}
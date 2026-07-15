package com.rick.smartparkingplatform.simulation.operation;

import com.rick.smartparkingplatform.entity.Parking;

import com.rick.smartparkingplatform.service.ParkingService;
import com.rick.smartparkingplatform.simulation.enums.SimulationState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class OperatingHoursService {

    private final ParkingService parkingService;

    private SimulationState calculateState(LocalTime simulationTime, Parking parking) {
        LocalTime openingTime = parking.getOpeningTime();
        LocalTime closingTime = parking.getClosingTime();

        if (!simulationTime.isBefore(openingTime) && simulationTime.isBefore(closingTime)) {
            return SimulationState.OPEN;
        }

        return SimulationState.CLOSED;
    }

    public SimulationState getCurrentState(LocalDateTime currentTime) {

        Parking parking = parkingService.getCurrentParking();

        return calculateState(currentTime.toLocalTime(), parking);
    }
    

}

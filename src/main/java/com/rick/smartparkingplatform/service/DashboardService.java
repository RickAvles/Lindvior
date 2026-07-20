package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.response.DashboardOccupancyResponse;
import com.rick.smartparkingplatform.dto.response.DashboardResponse;
import com.rick.smartparkingplatform.dto.response.DashboardSimulationResponse;
import com.rick.smartparkingplatform.dto.response.OccupancyResponse;
import com.rick.smartparkingplatform.simulation.conditions.ConditionService;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import com.rick.smartparkingplatform.simulation.operation.OperatingHoursService;
import com.rick.smartparkingplatform.simulation.operation.SimulationState;
import com.rick.smartparkingplatform.simulation.queue.EntryQueueService;
import com.rick.smartparkingplatform.simulation.queue.ExitQueueService;
import com.rick.smartparkingplatform.simulation.queue.ParkingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SimulationClock simulationClock;
    private final OperatingHoursService operatingHoursService;
    private final ParkingSpotService parkingSpotService;
    private final EntryQueueService entryQueueService;
    private final ConditionService conditionService;

    private final ParkingQueueService parkingQueueService;

    private final ExitQueueService exitQueueService;

    /**
     * Converte os dados da simulação para o DTO de resposta.
     */
    private DashboardSimulationResponse toSimulationResponse(
            LocalDateTime currentTime,
            SimulationState simulationState) {

        return new DashboardSimulationResponse(
                currentTime,
                simulationState,
                conditionService.getCurrentDayType(),
                conditionService.getCurrentWeather()
        );
    }

    /**
     * Converte os dados de ocupação para o DTO de resposta.
     */
    private DashboardOccupancyResponse toOccupancyResponse(
            OccupancyResponse occupancy) {

        return new DashboardOccupancyResponse(
                occupancy.totalSpots(),
                occupancy.availableSpots(),
                occupancy.occupiedSpots(),
                occupancy.occupancyRate(),
                entryQueueService.size(),
                parkingQueueService.size(),
                exitQueueService.size()
        );
    }

    /**
     * Retorna o resumo operacional utilizado pelo dashboard.
     */
    public DashboardResponse getDashboard() {

        LocalDateTime currentTime =
                simulationClock.getCurrentTime();

        SimulationState simulationState =
                operatingHoursService.getCurrentState(currentTime);

        OccupancyResponse occupancy =
                parkingSpotService.getOccupancy();

        return new DashboardResponse(
                toSimulationResponse(
                        currentTime,
                        simulationState
                ),
                toOccupancyResponse(
                        occupancy
                )
        );
    }

}
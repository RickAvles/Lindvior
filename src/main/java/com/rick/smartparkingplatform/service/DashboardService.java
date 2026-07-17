package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.response.DashboardOccupancyResponse;
import com.rick.smartparkingplatform.dto.response.DashboardResponse;
import com.rick.smartparkingplatform.dto.response.DashboardSimulationResponse;
import com.rick.smartparkingplatform.dto.response.OccupancyResponse;
import com.rick.smartparkingplatform.simulation.clock.SimulationClock;
import com.rick.smartparkingplatform.simulation.enums.SimulationState;
import com.rick.smartparkingplatform.simulation.operation.OperatingHoursService;
import com.rick.smartparkingplatform.simulation.service.EnteringQueueService;
import com.rick.smartparkingplatform.simulation.service.EntryQueueService;
import com.rick.smartparkingplatform.simulation.service.ExitQueueService;
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

    private final EnteringQueueService enteringQueueService;

    private final ExitQueueService exitQueueService;

    /**
     * Converte os dados da simulação para o DTO de resposta.
     */
    private DashboardSimulationResponse toSimulationResponse(
            LocalDateTime currentTime,
            SimulationState simulationState) {

        return new DashboardSimulationResponse(
                currentTime,
                simulationState
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
                enteringQueueService.size(),
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
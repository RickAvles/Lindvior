package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.response.*;
import com.rick.smartparkingplatform.simulation.conditions.ConditionService;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import com.rick.smartparkingplatform.simulation.gate.EntryGateManager;
import com.rick.smartparkingplatform.simulation.gate.ExitGateManager;
import com.rick.smartparkingplatform.simulation.gate.Gate;
import com.rick.smartparkingplatform.simulation.operation.OperatingHoursService;
import com.rick.smartparkingplatform.simulation.operation.SimulationState;
import com.rick.smartparkingplatform.simulation.queue.EntryQueueService;
import com.rick.smartparkingplatform.simulation.queue.ExitQueueService;
import com.rick.smartparkingplatform.simulation.queue.ParkingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    // Serviços da simulação.
    private final SimulationClock simulationClock;
    private final OperatingHoursService operatingHoursService;
    private final ConditionService conditionService;

    // Serviços de ocupação.
    private final ParkingSpotService parkingSpotService;
    private final EntryQueueService entryQueueService;
    private final ParkingQueueService parkingQueueService;
    private final ExitQueueService exitQueueService;

    // Gerenciadores das cancelas.
    private final EntryGateManager entryGateManager;
    private final ExitGateManager exitGateManager;

    // Converte os dados da simulação.
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

    // Converte as cancelas para o dashboard.
    private List<GateResponse> toGateResponse(
            List<Gate> gates,
            String prefix) {

        return gates.stream()
                .map(gate -> {

                    String vehiclePlate = "";

                    if (gate.getCurrentSession() != null) {
                        vehiclePlate = gate.getCurrentSession()
                                .getVehicle()
                                .getLicensePlate();
                    }

                    return new GateResponse(
                            prefix + gate.getNumber(),
                            gate.isAvailable(simulationClock.getCurrentTime()),
                            vehiclePlate
                    );
                })
                .toList();
    }

    // Converte os dados de ocupação.
    private DashboardOccupancyResponse toOccupancyResponse(
            OccupancyResponse occupancy) {

        return new DashboardOccupancyResponse(
                occupancy.totalSpots(),
                occupancy.availableSpots(),
                occupancy.occupiedSpots(),
                occupancy.occupancyRate(),
                entryQueueService.size(),
                parkingQueueService.size(),
                exitQueueService.size(),
                toGateResponse(
                        entryGateManager.getGates(),
                        "E"
                ),
                toGateResponse(
                        exitGateManager.getGates(),
                        "S"
                )
        );

    }

    // Retorna os dados do dashboard.
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
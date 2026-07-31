package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.response.DashboardOccupancyResponse;
import com.rick.smartparkingplatform.dto.response.DashboardResponse;
import com.rick.smartparkingplatform.dto.response.DashboardSimulationResponse;
import com.rick.smartparkingplatform.dto.response.GateResponse;
import com.rick.smartparkingplatform.simulation.dashboard.DashboardState;
import com.rick.smartparkingplatform.simulation.metrics.statistics.GateMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardStateService dashboardStateService;

    // Converte as métricas das cancelas para a resposta da API.
    private List<GateResponse> toGateResponse(List<GateMetrics> gates) {

        return gates.stream()
                .map(gate -> new GateResponse(
                        gate.gate(),
                        gate.available(),
                        gate.vehiclePlate()
                ))
                .toList();
    }

    // Converte os dados da simulação.
    private DashboardSimulationResponse toSimulationResponse(
            DashboardState dashboard) {

        return new DashboardSimulationResponse(
                dashboard.clock().currentTime(),
                dashboard.clock().simulationState(),
                dashboard.conditions().dayType(),
                dashboard.conditions().weather()
        );

    }

    // Converte os dados de ocupação.
    private DashboardOccupancyResponse toOccupancyResponse(
            DashboardState dashboard) {

        return new DashboardOccupancyResponse(
                dashboard.parking().totalSpots(),
                dashboard.parking().availableSpots(),
                dashboard.parking().occupiedSpots(),
                dashboard.parking().occupancyRate(),
                dashboard.parking().entryQueue(),
                dashboard.parking().parkingQueue(),
                dashboard.parking().exitQueue(),
                toGateResponse(
                        dashboard.statistics().entryGates()
                ),
                toGateResponse(
                        dashboard.statistics().exitGates()
                )
        );

    }

    // Retorna os dados do dashboard.
    public DashboardResponse getDashboard() {

        DashboardState dashboard = dashboardStateService.getState();

        return new DashboardResponse(
                toSimulationResponse(dashboard),
                toOccupancyResponse(dashboard)
        );

    }

}
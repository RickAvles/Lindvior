package com.rick.smartparkingplatform.mapper;

import com.rick.smartparkingplatform.dto.response.DashboardOccupancyResponse;
import com.rick.smartparkingplatform.dto.response.DashboardResponse;
import com.rick.smartparkingplatform.dto.response.DashboardSimulationResponse;
import com.rick.smartparkingplatform.dto.response.GateResponse;
import com.rick.smartparkingplatform.simulation.dashboard.DashboardState;
import com.rick.smartparkingplatform.simulation.metrics.statistics.GateMetrics;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DashboardResponseFactory {

    public DashboardResponse create(DashboardState dashboard) {

        return new DashboardResponse(
                toSimulationResponse(dashboard),
                toOccupancyResponse(dashboard)
        );

    }

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
                toGateResponse(dashboard.statistics().entryGates()),
                toGateResponse(dashboard.statistics().exitGates())
        );

    }

}
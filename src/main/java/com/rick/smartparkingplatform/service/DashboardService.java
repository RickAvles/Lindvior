package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.dto.response.DashboardOccupancyResponse;
import com.rick.smartparkingplatform.dto.response.DashboardResponse;
import com.rick.smartparkingplatform.dto.response.DashboardSimulationResponse;
import com.rick.smartparkingplatform.dto.response.GateResponse;
import com.rick.smartparkingplatform.simulation.metrics.dashboard.SimulationMetrics;
import com.rick.smartparkingplatform.simulation.metrics.dashboard.SimulationMetricsService;
import com.rick.smartparkingplatform.simulation.metrics.statistics.GateMetrics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SimulationMetricsService simulationMetricsService;

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
            SimulationMetrics metrics) {

        return new DashboardSimulationResponse(
                metrics.currentTime(),
                metrics.simulationState(),
                metrics.currentDayType(),
                metrics.currentWeather()
        );
    }

    // Converte os dados de ocupação.
    private DashboardOccupancyResponse toOccupancyResponse(
            SimulationMetrics metrics) {

        return new DashboardOccupancyResponse(
                metrics.totalSpots(),
                metrics.availableSpots(),
                metrics.occupiedSpots(),
                metrics.occupancyRate(),
                metrics.entryQueue(),
                metrics.parkingQueue(),
                metrics.exitQueue(),
                toGateResponse(metrics.entryGates()),
                toGateResponse(metrics.exitGates())
        );
    }


    // Retorna os dados do dashboard.
    public DashboardResponse getDashboard() {

        SimulationMetrics metrics =
                simulationMetricsService.getCurrentMetrics();

        return new DashboardResponse(
                toSimulationResponse(metrics),
                toOccupancyResponse(metrics)
        );
    }

}
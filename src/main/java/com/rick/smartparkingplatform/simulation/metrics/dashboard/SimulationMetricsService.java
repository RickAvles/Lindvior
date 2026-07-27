package com.rick.smartparkingplatform.simulation.metrics.dashboard;

import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import com.rick.smartparkingplatform.simulation.conditions.ConditionService;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import com.rick.smartparkingplatform.simulation.gate.EntryGateManager;
import com.rick.smartparkingplatform.simulation.gate.ExitGateManager;
import com.rick.smartparkingplatform.simulation.gate.Gate;
import com.rick.smartparkingplatform.simulation.metrics.statistics.GateMetrics;
import com.rick.smartparkingplatform.simulation.metrics.statistics.ParkingOccupancy;
import com.rick.smartparkingplatform.simulation.metrics.statistics.SimulationStatisticsService;
import com.rick.smartparkingplatform.simulation.operation.OperatingHoursService;
import com.rick.smartparkingplatform.simulation.queue.EntryQueueService;
import com.rick.smartparkingplatform.simulation.queue.ExitQueueService;
import com.rick.smartparkingplatform.simulation.queue.ParkingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SimulationMetricsService {

    // Simulation
    private final SimulationClock simulationClock;
    private final OperatingHoursService operatingHoursService;
    private final ConditionService conditionService;

    // Domain
    private final ParkingSpotService parkingSpotService;
    private final ParkingSessionService parkingSessionService;

    // Queues
    private final EntryQueueService entryQueueService;
    private final ParkingQueueService parkingQueueService;
    private final ExitQueueService exitQueueService;

    // Gates
    private final EntryGateManager entryGateManager;
    private final ExitGateManager exitGateManager;

    // Statistics
    private final SimulationStatisticsService simulationStatisticsService;

    private SimulationMetrics currentMetrics;

    // Atualiza o snapshot da simulação.
    public void update() {

        LocalDateTime currentTime = simulationClock.getCurrentTime();

        ParkingOccupancy occupancy = parkingSpotService.getParkingOccupancy();

        currentMetrics = new SimulationMetrics(

                // Tempo.
                currentTime,

                // Estado operacional.
                operatingHoursService.getCurrentState(currentTime),

                // Condições.
                conditionService.getCurrentDayType(),
                conditionService.getCurrentWeather(),

                // Ocupação.
                occupancy.totalSpots(),
                occupancy.availableSpots(),
                occupancy.occupiedSpots(),
                occupancy.occupancyRate(),

                // Filas.
                entryQueueService.size(),
                parkingQueueService.size(),
                exitQueueService.size(),

                // Sessões.
                parkingSessionService.countActiveSessions(),
                parkingSessionService.countEnteringSessions(),
                parkingSessionService.countExitingSessions(),

                // Estatísticas acumuladas.
                parkingSessionService.countCompletedSessions(),

                // Indicadores.
                simulationStatisticsService.getAverageStay(),

                simulationStatisticsService.getAverageEntryWait(),

                simulationStatisticsService.getAverageParkingWait(),

                simulationStatisticsService.getAverageExitWait(),

                simulationStatisticsService.getEntryFlowRate(),

                simulationStatisticsService.getExitFlowRate(),
                
                // Cancelas.
                toGateMetrics(
                        entryGateManager.getGates(),
                        currentTime,
                        "E"
                ),
                toGateMetrics(
                        exitGateManager.getGates(),
                        currentTime,
                        "S"
                )

        );
    }

    // Converte as cancelas para métricas.
    private List<GateMetrics> toGateMetrics(
            List<Gate> gates,
            LocalDateTime currentTime,
            String prefix) {

        return gates.stream()
                .map(gate -> {

                    String vehiclePlate = "";

                    if (gate.getCurrentSession() != null) {
                        vehiclePlate = gate.getCurrentSession()
                                .getVehicle()
                                .getLicensePlate();
                    }

                    return new GateMetrics(
                            prefix + gate.getNumber(),
                            gate.isAvailable(currentTime),
                            vehiclePlate
                    );
                })
                .toList();
    }

    // Retorna o snapshot atual da simulação.
    public SimulationMetrics getCurrentMetrics() {

        if (currentMetrics == null) {
            update();
        }

        return currentMetrics;
    }

}
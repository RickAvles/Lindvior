package com.rick.smartparkingplatform.simulation.metrics.dashboard;

import com.rick.smartparkingplatform.notification.NotificationEvent;
import com.rick.smartparkingplatform.notification.NotificationSeverity;
import com.rick.smartparkingplatform.notification.NotificationType;
import com.rick.smartparkingplatform.notification.producer.NotificationEventProducer;
import com.rick.smartparkingplatform.service.DashboardStateService;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.service.ParkingSpotService;
import com.rick.smartparkingplatform.simulation.conditions.ConditionService;
import com.rick.smartparkingplatform.simulation.dashboard.DashboardPublisher;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardClock;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardParking;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardStatistics;
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

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SimulationMetricsService {

    private static final BigDecimal OCCUPANCY_25_PERCENT = BigDecimal.valueOf(25);

    private static final BigDecimal OCCUPANCY_50_PERCENT = BigDecimal.valueOf(50);

    private static final BigDecimal OCCUPANCY_75_PERCENT = BigDecimal.valueOf(75);

    private static final BigDecimal OCCUPANCY_FULL = BigDecimal.valueOf(100);

    private static final Duration NOTIFICATION_COOLDOWN = Duration.ofMinutes(3);

    private final Map<NotificationType, LocalDateTime> lastNotificationTimes = new EnumMap<>(NotificationType.class);


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

    // Dashboard
    private final DashboardStateService dashboardStateService;
    private final DashboardPublisher dashboardPublisher;

    // Notifications
    private final NotificationEventProducer notificationEventProducer;

    private SimulationMetrics currentMetrics;

    // Armazena a ocupação registrada no tick anterior.
    private BigDecimal previousOccupancyRate;

    // Atualiza o snapshot da simulação.
    public void update() {

        LocalDateTime currentTime = simulationClock.getCurrentTime();

        ParkingOccupancy occupancy =
                parkingSpotService.getParkingOccupancy();

        // Verifica se a ocupação ultrapassou algum dos limites de notificação.
        publishOccupancyNotifications(
                previousOccupancyRate,
                occupancy.occupancyRate(),
                currentTime
        );

        // Armazena a ocupação atual para comparação no próximo tick.
        previousOccupancyRate = occupancy.occupancyRate();

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

        dashboardStateService.updateParking(
                new DashboardParking(
                        currentMetrics.totalSpots(),
                        currentMetrics.availableSpots(),
                        currentMetrics.occupiedSpots(),
                        currentMetrics.occupancyRate(),

                        currentMetrics.entryQueue(),
                        currentMetrics.parkingQueue(),
                        currentMetrics.exitQueue(),

                        currentMetrics.activeSessions(),
                        currentMetrics.enteringSessions(),
                        currentMetrics.exitingSessions()
                )
        );

        dashboardStateService.updateStatistics(
                new DashboardStatistics(
                        currentMetrics.completedSessions(),
                        currentMetrics.averageStay(),
                        currentMetrics.averageEntryWait(),
                        currentMetrics.averageParkingWait(),
                        currentMetrics.averageExitWait(),
                        currentMetrics.entryFlowRate(),
                        currentMetrics.exitFlowRate(),
                        currentMetrics.entryGates(),
                        currentMetrics.exitGates()
                )
        );

        dashboardStateService.updateClock(
                new DashboardClock(
                        currentMetrics.currentTime(),
                        currentMetrics.simulationState()
                )
        );

        dashboardPublisher.publish();
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

    // Publica notificações quando a ocupação ultrapassa os limites definidos.
    private void publishOccupancyNotifications(
            BigDecimal previousRate,
            BigDecimal currentRate,
            LocalDateTime currentTime) {

// Publica a notificação quando a ocupação cruza 25%,
// respeitando o cooldown configurado para esse tipo de evento.
        if (crossedThreshold(previousRate, currentRate, OCCUPANCY_25_PERCENT)
                && canPublishNotification(
                NotificationType.PARKING_25_PERCENT,
                currentTime)) {

            notificationEventProducer.send(
                    new NotificationEvent(
                            NotificationType.PARKING_25_PERCENT,
                            NotificationSeverity.INFO,
                            "Parking occupancy reached 25%.",
                            currentTime
                    )
            );
        }

// Publica a notificação quando a ocupação cruza 50%,
// respeitando o cooldown configurado para esse tipo de evento.
        if (crossedThreshold(previousRate, currentRate, OCCUPANCY_50_PERCENT)
                && canPublishNotification(
                NotificationType.PARKING_50_PERCENT,
                currentTime)) {

            notificationEventProducer.send(
                    new NotificationEvent(
                            NotificationType.PARKING_50_PERCENT,
                            NotificationSeverity.INFO,
                            "Parking occupancy reached 50%.",
                            currentTime
                    )
            );
        }

// Publica a notificação quando a ocupação cruza 75%,
// respeitando o cooldown configurado para esse tipo de evento.
        if (crossedThreshold(previousRate, currentRate, OCCUPANCY_75_PERCENT)
                && canPublishNotification(
                NotificationType.PARKING_75_PERCENT,
                currentTime)) {

            notificationEventProducer.send(
                    new NotificationEvent(
                            NotificationType.PARKING_75_PERCENT,
                            NotificationSeverity.WARNING,
                            "Parking occupancy reached 75%.",
                            currentTime
                    )
            );
        }

// Publica a notificação quando a ocupação atinge 100%,
// respeitando o cooldown configurado para esse tipo de evento.
        if (crossedThreshold(previousRate, currentRate, OCCUPANCY_FULL)
                && canPublishNotification(
                NotificationType.PARKING_FULL,
                currentTime)) {

            notificationEventProducer.send(
                    new NotificationEvent(
                            NotificationType.PARKING_FULL,
                            NotificationSeverity.CRITICAL,
                            "Parking is full.",
                            currentTime
                    )
            );
        }
    }

    // Verifica se a ocupação cruzou o limite entre dois ticks.
    private boolean crossedThreshold(
            BigDecimal previousRate,
            BigDecimal currentRate,
            BigDecimal threshold) {

        if (previousRate == null) {
            return false;
        }

        return previousRate.compareTo(threshold) < 0
                && currentRate.compareTo(threshold) >= 0;
    }

    // Verifica se a notificação do tipo informado pode ser publicada novamente.
    private boolean canPublishNotification(NotificationType notificationType, LocalDateTime currentTime) {

        LocalDateTime lastNotificationTime = lastNotificationTimes.get(notificationType);

        // A primeira ocorrência do tipo de notificação pode ser publicada.
        if (lastNotificationTime == null) {
            lastNotificationTimes.put(notificationType, currentTime);
            return true;
        }

        // Verifica quanto tempo passou desde a última publicação.
        Duration elapsedTime = Duration.between(lastNotificationTime, currentTime);

        // Bloqueia novas notificações durante o período de cooldown.
        if (elapsedTime.compareTo(NOTIFICATION_COOLDOWN) < 0) {
            return false;
        }

        // O cooldown terminou, então registra a nova publicação.
        lastNotificationTimes.put(notificationType, currentTime);

        return true;
    }
}
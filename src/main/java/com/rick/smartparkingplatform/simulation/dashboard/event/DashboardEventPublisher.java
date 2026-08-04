package com.rick.smartparkingplatform.simulation.dashboard.event;

import com.rick.smartparkingplatform.config.websocket.WebSocketTopics;
import com.rick.smartparkingplatform.dto.dashboard.DashboardVehicle;
import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.entity.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardEventPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    // Publica um evento de entrada no estacionamento.
    public void publishVehicleEntered(ParkingSession parkingSession) {

        publish(
                DashboardEventType.VEHICLE_ENTERED,
                parkingSession,
                new DashboardGateEvent(
                        parkingSession.getEntryGate().getNumber()
                ),
                null
        );
    }

    // Publica um evento de estacionamento.
    public void publishVehicleParked(ParkingSession parkingSession) {

        publish(
                DashboardEventType.VEHICLE_PARKED,
                parkingSession,
                null,
                new DashboardSpotEvent(
                        parkingSession.getParkingSpot().getCode()
                )
        );
    }

    // Publica um evento de saída da vaga.
    public void publishVehicleLeftSpot(ParkingSession parkingSession) {

        publish(DashboardEventType.VEHICLE_LEFT_SPOT,
                parkingSession,
                null,
                new DashboardSpotEvent(
                        parkingSession.getParkingSpot().getCode()
                )
        );
    }

    // Publica um evento de saída do estacionamento.
    public void publishVehicleExited(ParkingSession parkingSession) {

        publish(
                DashboardEventType.VEHICLE_EXITED,
                parkingSession,
                new DashboardGateEvent(parkingSession.getExitGate().getNumber()),
                null
        );
    }

    // Constrói e publica um evento da dashboard.
    private void publish(DashboardEventType type, ParkingSession parkingSession, DashboardGateEvent gate, DashboardSpotEvent spot) {

        DashboardEvent event = new DashboardEvent(
                type,
                buildVehicle(parkingSession),
                gate,
                spot
        );

        send(event);
    }

    // Envia um evento para os clientes conectados.
    private void send(DashboardEvent event) {

        messagingTemplate.convertAndSend(WebSocketTopics.DASHBOARD_EVENTS, event);
    }

    // Constrói o veículo do evento.
    private DashboardVehicle buildVehicle(ParkingSession parkingSession) {

        Vehicle vehicle = parkingSession.getVehicle();

        return new DashboardVehicle(
                vehicle.getLicensePlate(),
                vehicle.getType(),
                vehicle.getColor(),
                parkingSession.getEntryTime()
        );
    }

}
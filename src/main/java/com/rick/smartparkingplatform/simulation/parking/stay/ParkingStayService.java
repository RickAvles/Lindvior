package com.rick.smartparkingplatform.simulation.parking.stay;

import com.rick.smartparkingplatform.entity.ParkingSession;
import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.simulation.engine.SimulationClock;
import com.rick.smartparkingplatform.simulation.log.SimulationLogger;
import com.rick.smartparkingplatform.simulation.parking.exit.ParkingExitService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingStayService {

    private final ParkingStayManager parkingStayManager;
    private final ParkingSessionService parkingSessionService;
    private final ParkingExitService parkingExitService;

    private final SimulationClock simulationClock;
    private final SimulationLogger simulationLogger;

    @Transactional
    public void process() {

        LocalDateTime currentTime = simulationClock.getCurrentTime();

        processStay(currentTime);
    }

    // Processa as sessões estacionadas e decide quais veículos irão deixar a vaga.
    private void processStay(LocalDateTime currentTime) {


        List<ParkingSession> activeSessions = parkingSessionService.getActiveSessions();

        for (ParkingSession parkingSession : activeSessions) {

            StayDecision decision = parkingStayManager.evaluate(parkingSession, currentTime);

            if (!decision.shouldExit()) {
                continue;
            }

            simulationLogger.exit(parkingSession.getVehicle().getLicensePlate(), decision.probability());

            parkingExitService.startExit(parkingSession);
        }
    }

}
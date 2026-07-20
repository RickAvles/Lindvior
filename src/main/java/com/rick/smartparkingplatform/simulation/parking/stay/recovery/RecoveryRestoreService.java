package com.rick.smartparkingplatform.simulation.parking.stay.recovery;

import com.rick.smartparkingplatform.service.ParkingSessionService;
import com.rick.smartparkingplatform.simulation.queue.ParkingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecoveryRestoreService {

    private final ParkingSessionService parkingSessionService;
    private final ParkingQueueService parkingQueueService;

    // Restaura as sessões que estavam entrando.
    public void restoreEnteringSessions() {

        parkingSessionService.getEnteringSessions().forEach(parkingQueueService::enqueue);
    }

}
package com.rick.smartparkingplatform.simulation.dashboard;

import com.rick.smartparkingplatform.dto.response.DashboardResponse;
import com.rick.smartparkingplatform.mapper.DashboardResponseFactory;
import com.rick.smartparkingplatform.service.DashboardStateService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DashboardPublisher {

    private final DashboardStateService dashboardStateService;
    private final DashboardResponseFactory dashboardResponseFactory;
    private final SimpMessagingTemplate messagingTemplate;

    public void publish() {

        DashboardState dashboard = dashboardStateService.getState();

        DashboardResponse response = dashboardResponseFactory.create(dashboard);

        messagingTemplate.convertAndSend(
                "/topic/dashboard",
                response
        );

    }
    

}
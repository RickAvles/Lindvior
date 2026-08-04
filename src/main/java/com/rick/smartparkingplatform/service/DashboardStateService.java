package com.rick.smartparkingplatform.service;

import com.rick.smartparkingplatform.repository.DashboardStateRepository;
import com.rick.smartparkingplatform.simulation.dashboard.DashboardState;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardClock;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardConditions;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardParking;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardStateService {

    private final DashboardStateRepository dashboardStateRepository;

    public void updateClock(DashboardClock dashboardClock) {

        dashboardStateRepository.saveClock(dashboardClock);

    }

    public void updateConditions(DashboardConditions dashboardConditions) {

        dashboardStateRepository.saveConditions(dashboardConditions);

    }

    public void updateParking(DashboardParking dashboardParking) {

        dashboardStateRepository.saveParking(dashboardParking);

    }

    public void updateStatistics(DashboardStatistics dashboardStatistics) {

        dashboardStateRepository.saveStatistics(dashboardStatistics);

    }

    public DashboardState getState() {

        return new DashboardState(
                dashboardStateRepository.getClock(),
                dashboardStateRepository.getConditions(),
                dashboardStateRepository.getParking(),
                dashboardStateRepository.getStatistics(),
                dashboardStateRepository.getLayout()

        );

    }

}
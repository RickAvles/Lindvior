package com.rick.smartparkingplatform.simulation.dashboard;

import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardClock;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardConditions;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardParking;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardStatistics;

public record DashboardState(

        DashboardClock clock,

        DashboardConditions conditions,

        DashboardParking parking,

        DashboardStatistics statistics

) {
}
package com.rick.smartparkingplatform.repository;

import com.rick.smartparkingplatform.simulation.dashboard.DashboardKeys;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardClock;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardConditions;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardParking;
import com.rick.smartparkingplatform.simulation.dashboard.model.DashboardStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class DashboardStateRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public void saveClock(DashboardClock dashboardClock) {

        redisTemplate.opsForValue().set(
                DashboardKeys.CLOCK,
                dashboardClock
        );

    }

    public DashboardClock getClock() {

        return (DashboardClock) redisTemplate
                .opsForValue()
                .get(DashboardKeys.CLOCK);

    }

    public void saveConditions(DashboardConditions dashboardConditions) {

        redisTemplate.opsForValue().set(
                DashboardKeys.CONDITIONS,
                dashboardConditions
        );

    }

    public DashboardConditions getConditions() {

        return (DashboardConditions) redisTemplate
                .opsForValue()
                .get(DashboardKeys.CONDITIONS);

    }

    public void saveParking(DashboardParking dashboardParking) {

        redisTemplate.opsForValue().set(
                DashboardKeys.PARKING,
                dashboardParking
        );

    }

    public DashboardParking getParking() {

        return (DashboardParking) redisTemplate
                .opsForValue()
                .get(DashboardKeys.PARKING);

    }

    public void saveStatistics(DashboardStatistics dashboardStatistics) {

        redisTemplate.opsForValue().set(
                DashboardKeys.STATISTICS,
                dashboardStatistics
        );

    }

    public DashboardStatistics getStatistics() {

        return (DashboardStatistics) redisTemplate
                .opsForValue()
                .get(DashboardKeys.STATISTICS);

    }
}
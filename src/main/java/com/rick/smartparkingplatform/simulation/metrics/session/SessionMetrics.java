package com.rick.smartparkingplatform.simulation.metrics.session;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class SessionMetrics {

    private LocalDateTime entryQueueAt;
    private LocalDateTime entryGateAt;
    private LocalDateTime parkedAt;

    private LocalDateTime exitQueueAt;
    private LocalDateTime exitGateAt;
    private LocalDateTime finishedAt;

}
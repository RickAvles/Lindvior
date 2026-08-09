package com.rick.smartparkingplatform.dto.messaging;

import java.time.LocalDate;

public record DailyReportMessage(
        LocalDate reportDate
) {
}

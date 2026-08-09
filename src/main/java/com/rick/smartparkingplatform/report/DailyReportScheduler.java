package com.rick.smartparkingplatform.report;

import com.rick.smartparkingplatform.config.rabbitmq.producer.DailyReportProducer;
import com.rick.smartparkingplatform.dto.messaging.DailyReportMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class DailyReportScheduler {

    private final DailyReportProducer dailyReportProducer;

    // Solicita a geração do relatório do dia operacional encerrado.
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDailyReport() {

        LocalDate reportDate = LocalDate.now().minusDays(1);

        dailyReportProducer.send(new DailyReportMessage(reportDate));
    }
}
package com.rick.smartparkingplatform.config.rabbitmq.consumer;

import com.rick.smartparkingplatform.constant.RabbitMQConstants;
import com.rick.smartparkingplatform.dto.messaging.DailyReportMessage;
import com.rick.smartparkingplatform.service.ReportService;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class DailyReportConsumer {

    private final ReportService reportService;

    // Consome a solicitação e gera o relatório diário.
    @RabbitListener(queues = RabbitMQConstants.DAILY_REPORT_QUEUE)
    public void consume(DailyReportMessage message) throws IOException {

        reportService.generateDailyReportPdf(message.reportDate());
    }
}
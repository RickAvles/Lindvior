package com.rick.smartparkingplatform.config.rabbitmq.producer;

import com.rick.smartparkingplatform.constant.RabbitMQConstants;
import com.rick.smartparkingplatform.dto.messaging.DailyReportMessage;
import lombok.AllArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class DailyReportProducer {

    private final RabbitTemplate rabbitTemplate;

    // Publica a solicitação de geração do relatório diário.
    public void send(DailyReportMessage message) {

        rabbitTemplate.convertAndSend(
                RabbitMQConstants.DAILY_REPORT_EXCHANGE,
                RabbitMQConstants.DAILY_REPORT_ROUTING_KEY,
                message
        );
    }

}
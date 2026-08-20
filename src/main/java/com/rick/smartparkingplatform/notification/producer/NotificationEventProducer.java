package com.rick.smartparkingplatform.notification.producer;

import com.rick.smartparkingplatform.constant.KafkaConstants;
import com.rick.smartparkingplatform.notification.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventProducer {

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    // Publica um evento de notificação no Kafka.
    public void send(NotificationEvent event) {

        kafkaTemplate.send(
                KafkaConstants.NOTIFICATION_TOPIC,
                event
        );
    }
}
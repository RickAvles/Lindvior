package com.rick.smartparkingplatform.notification.consumer;

import com.rick.smartparkingplatform.constant.KafkaConstants;
import com.rick.smartparkingplatform.notification.NotificationEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    // Recebe os eventos de notificação publicados no Kafka.
    @KafkaListener(
            topics = KafkaConstants.NOTIFICATION_TOPIC,
            groupId = "lindvior-notification"
    )
    public void consume(NotificationEvent event) {

        // Temporariamente apenas confirma o recebimento do evento.
        System.out.println("Notification received: " + event);
    }
}

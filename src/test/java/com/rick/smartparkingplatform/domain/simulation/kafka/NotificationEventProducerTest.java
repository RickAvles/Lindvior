package com.rick.smartparkingplatform.domain.simulation.kafka;

import com.rick.smartparkingplatform.notification.NotificationEvent;
import com.rick.smartparkingplatform.notification.NotificationSeverity;
import com.rick.smartparkingplatform.notification.NotificationType;
import com.rick.smartparkingplatform.notification.producer.NotificationEventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;

import static com.rick.smartparkingplatform.constant.KafkaConstants.NOTIFICATION_TOPIC;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class NotificationEventProducerTest {

    @Test
    void shouldPublishNotificationEvent() {

        KafkaTemplate<String, NotificationEvent> kafkaTemplate =
                mock(KafkaTemplate.class);

        NotificationEventProducer producer =
                new NotificationEventProducer(kafkaTemplate);

        NotificationEvent event = new NotificationEvent(
                NotificationType.PARKING_50_PERCENT,
                NotificationSeverity.INFO,
                "Parking occupancy reached 50%.",
                LocalDateTime.now()
        );

        producer.send(event);

        verify(kafkaTemplate).send(
                NOTIFICATION_TOPIC,
                event
        );
    }
}
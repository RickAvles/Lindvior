package com.rick.smartparkingplatform.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

import static com.rick.smartparkingplatform.constant.KafkaConstants.NOTIFICATION_TOPIC;

@Configuration
public class KafkaConfig {

    // Cria o tópico responsável pelos eventos de notificação.
    @Bean
    public NewTopic notificationTopic() {

        return TopicBuilder
                .name(NOTIFICATION_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
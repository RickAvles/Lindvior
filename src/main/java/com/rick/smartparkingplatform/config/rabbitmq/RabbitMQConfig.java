package com.rick.smartparkingplatform.config.rabbitmq;

import com.rick.smartparkingplatform.constant.RabbitMQConstants;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Cria a fila responsável pelo relatório diário.
    @Bean
    public Queue dailyReportQueue() {

        return new Queue(RabbitMQConstants.DAILY_REPORT_QUEUE, true);
    }

    // Cria a Exchange que receberá as mensagens.
    @Bean
    public DirectExchange dailyReportExchange() {

        return new DirectExchange(RabbitMQConstants.DAILY_REPORT_EXCHANGE);
    }

    // Liga a Exchange à fila através da Routing Key.
    @Bean
    public Binding dailyReportBinding(Queue dailyReportQueue, DirectExchange dailyReportExchange) {

        return BindingBuilder
                .bind(dailyReportQueue)
                .to(dailyReportExchange)
                .with(RabbitMQConstants.DAILY_REPORT_ROUTING_KEY);
    }

    // Garante a declaração da infraestrutura no RabbitMQ.
    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {

        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);

        return rabbitAdmin;
    }

    // Configura a conversão das mensagens para JSON.
    @Bean
    public JacksonJsonMessageConverter jacksonJsonMessageConverter() {

        return new JacksonJsonMessageConverter();
    }
}
package com.example.shop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig {

    public static final String ORDER_EXCHANGE = "shop.order.exchange";
    public static final String ORDER_QUEUE = "shop.order.queue";
    public static final String ORDER_ROUTING_KEY = "shop.order.created";

    @Bean
    public TopicExchange orderExchange() {
        log.info("Создание TopicExchange: name={}", ORDER_EXCHANGE);
        return new TopicExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderQueue() {
        log.info("Создание очереди: name={}", ORDER_QUEUE);
        return QueueBuilder.durable(ORDER_QUEUE).build();
    }

    @Bean
    public Binding orderBinding() {
        log.info("Создание биндинга: queue={}, exchange={}, routingKey={}",
                ORDER_QUEUE, ORDER_EXCHANGE, ORDER_ROUTING_KEY);
        return BindingBuilder
                .bind(orderQueue())
                .to(orderExchange())
                .with(ORDER_ROUTING_KEY);
    }
}

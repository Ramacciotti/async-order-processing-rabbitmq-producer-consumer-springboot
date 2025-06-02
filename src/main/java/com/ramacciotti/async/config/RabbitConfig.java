package com.ramacciotti.async.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the queue where messages (orders) will be sent.
 */
@Slf4j
@Configuration
public class RabbitConfig {

    // The name of the queue where orders will be sent and received
    public static final String ORDER_QUEUE = "order.queue";

    /**
     * Creates the queue for sending and receiving order messages.
     * The queue is not persistent (messages are lost if RabbitMQ stops),
     * but that's okay for this simple example.
     */
    @Bean
    public Queue orderQueue() {
        log.info("Creating a queue called '{}'. This is where the messages will go!", ORDER_QUEUE);
        return new Queue(ORDER_QUEUE, false);
    }

}


package com.ramacciotti.async.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.interceptor.RetryOperationsInterceptor;

/**
 * Configures the queue where messages (orders) will be sent.
 */
@Slf4j
@Configuration
public class RabbitConfig {

    // Main order queue name - where new orders arrive
    public static final String ORDER_QUEUE = "order.queue";
    // Main exchange name - the channel that routes messages to queues
    public static final String ORDER_EXCHANGE = "order.exchange";
    // Routing key for order messages
    public static final String ORDER_ROUTING_KEY = "order.routingKey";

    // Dead Letter Queue (DLQ) name - where failed messages are sent
    public static final String DLQ_QUEUE = "order.dlq";
    // DLQ exchange name
    public static final String DLQ_EXCHANGE = "order.dlq.exchange";
    // Routing key for DLQ messages
    public static final String DLQ_ROUTING_KEY = "order.dlq.routingKey";

    @Bean
    public DirectExchange orderExchange() {
        log.info("Creating the main exchange (the channel that routes order messages)...");
        return new DirectExchange(ORDER_EXCHANGE);
    }

    @Bean
    public Queue orderQueue() {
        log.info("Creating the main order queue (where new orders will wait to be processed)...");

        // Configure DLQ: if message processing fails, message is sent to DLQ exchange with DLQ routing key
        return QueueBuilder.durable(ORDER_QUEUE)
                .withArgument("x-dead-letter-exchange", DLQ_EXCHANGE)        // Send failed messages here
                .withArgument("x-dead-letter-routing-key", DLQ_ROUTING_KEY)  // Using this routing key
                .build();
    }

    @Bean
    public Binding orderBinding() {
        log.info("Binding main queue to main exchange with routing key to receive order messages...");
        return BindingBuilder.bind(orderQueue())
                .to(orderExchange())
                .with(ORDER_ROUTING_KEY);
    }

    // DLQ exchange and queue setup

    @Bean
    public DirectExchange deadLetterExchange() {
        log.info("Creating Dead Letter Exchange (the channel for failed messages)...");
        return new DirectExchange(DLQ_EXCHANGE);
    }

    @Bean
    public Queue deadLetterQueue() {
        log.info("Creating Dead Letter Queue (where failed messages are stored for later inspection)...");
        return QueueBuilder.durable(DLQ_QUEUE).build();
    }

    @Bean
    public Binding deadLetterBinding() {
        log.info("Binding DLQ to DLQ exchange with routing key to receive failed messages...");
        return BindingBuilder.bind(deadLetterQueue())
                .to(deadLetterExchange())
                .with(DLQ_ROUTING_KEY);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory, Jackson2JsonMessageConverter jsonMessageConverter) {
        log.info("Creating RabbitListenerContainerFactory and configuring connection factory...");
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);

        log.info("Configuring retry interceptor: will attempt message processing up to 3 times (initial try + 2 retries)...");
        RetryOperationsInterceptor retryInterceptor = RetryInterceptorBuilder.stateless()
                .maxAttempts(3) // tries up to 3 times (1 initial + 2 retries)
                .backOffOptions(1000, 2.0, 10000) // waits 1s before first retry, doubles wait time after each retry, max wait 10s
                .recoverer(new RejectAndDontRequeueRecoverer()) // after retries exhausted, reject message and do NOT requeue (send to DLQ)
                .build();

        log.info("Setting retry interceptor as advice chain for RabbitListenerContainerFactory...");
        factory.setAdviceChain(retryInterceptor);

        // Configure JSON converter for factor (ESSENTIAL to deserialize Order automatically)
        factory.setMessageConverter(jsonMessageConverter);

        log.info("RabbitListenerContainerFactory configured successfully with retry and error handling...");
        return factory;
    }


}

package com.ramacciotti.async.producer;

import com.ramacciotti.async.config.RabbitConfig;
import com.ramacciotti.async.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

/**
 * Sends orders to the queue so that they can be processed later.
 */
@Slf4j
@Service
public class OrderProducer {

    private final RabbitTemplate rabbitTemplate;

    public OrderProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Sends the given order to the configured queue.
     */
    public void sendOrder(Order order) {
        log.info("Preparing to send order to queue: {}", order);
        rabbitTemplate.convertAndSend(RabbitConfig.ORDER_QUEUE, order);
        log.info("Order sent to queue successfully with ID: {}", order.getId());
    }
}

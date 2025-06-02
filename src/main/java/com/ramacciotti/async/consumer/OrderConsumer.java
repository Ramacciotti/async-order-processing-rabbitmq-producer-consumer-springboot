package com.ramacciotti.async.consumer;

import com.ramacciotti.async.config.RabbitConfig;
import com.ramacciotti.async.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Listens to the queue and receives orders automatically when they arrive.
 */
@Slf4j
@Service
public class OrderConsumer {

    /**
     * This method is called automatically whenever a new order message
     * arrives in the queue.
     */
    @RabbitListener(queues = RabbitConfig.ORDER_QUEUE)
    public void receiveOrder(Order order) {
        log.info("New order received from queue: {}", order);
        log.info("Now processing the order with id: {}", order.getId());
    }

}


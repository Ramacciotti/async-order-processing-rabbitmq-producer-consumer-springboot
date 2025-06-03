package com.ramacciotti.async.consumer;

import com.ramacciotti.async.config.RabbitConfig;
import com.ramacciotti.async.model.Order;
import com.ramacciotti.async.repository.OrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * Listens to the queue and receives orders automatically when they arrive.
 */
@Slf4j
@Service
public class OrderConsumer {

    private final OrderRepository orderRepository;

    public OrderConsumer(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    /**
     * This method is called automatically whenever a new order message
     * arrives in the queue.
     */
    @RabbitListener(queues = RabbitConfig.ORDER_QUEUE, containerFactory = "rabbitListenerContainerFactory") // bean that adds retry logic
    public void receiveOrder(Order order) {
        try {
            log.info("New order received in main queue: {}", order);
            if (order.getProduct().equalsIgnoreCase("fail")) {
                log.error("Simulating processing failure to test Dead Letter Queue...");
                throw new RuntimeException("Simulated error to send message to DLQ");
            }
            orderRepository.save(order);
            log.info("Order saved successfully on database!'");
        } catch (Exception exception) {
            log.error("Error processing order with id: {}", order.getId(), exception);
        }
    }

}


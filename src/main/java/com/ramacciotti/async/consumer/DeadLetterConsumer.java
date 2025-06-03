package com.ramacciotti.async.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ramacciotti.async.config.RabbitConfig;
import com.ramacciotti.async.model.Order;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class DeadLetterConsumer {

    // commented so we can see on rabbitMQ
    @RabbitListener(queues = RabbitConfig.DLQ_QUEUE)
    public void receiveDeadLetter(byte[] messageBody) throws JsonProcessingException {
        String json = new String(messageBody, StandardCharsets.UTF_8);
        log.info("Received message from DLQ: {}", json);
        Order order = new ObjectMapper().readValue(json, Order.class);
        log.info("Parsed Order from DLQ: {}", order);
    }

}

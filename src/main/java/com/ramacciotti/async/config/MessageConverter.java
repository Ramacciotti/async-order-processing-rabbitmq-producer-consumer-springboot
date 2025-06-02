package com.ramacciotti.async.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configures how messages are converted when sent to or received from the queue.
 * Uses JSON so it's easy to read and work with.
 */
@Slf4j
@Configuration
public class MessageConverter {

    /**
     * Creates a converter that changes Java objects to JSON automatically
     * when sending messages to the queue, and back to Java when receiving.
     */
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        log.info("Setting up JSON converter for messages...");
        return new Jackson2JsonMessageConverter();
    }

}

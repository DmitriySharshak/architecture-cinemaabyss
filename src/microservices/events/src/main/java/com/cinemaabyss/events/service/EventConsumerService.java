package com.cinemaabyss.events.service;

import com.cinemaabyss.events.model.Event;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumerService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "movie-events", groupId = "events-service")
    public void consumeMovieEvent(Event event) {
        logger.info("Consumed movie event: {} - {}", event.getId(), event.getType());
        logger.info("Movie event payload: {}", event.getPayload());
    }

    @KafkaListener(topics = "user-events", groupId = "events-service")
    public void consumeUserEvent(Event event) {
        logger.info("Consumed user event: {} - {}", event.getId(), event.getType());
        logger.info("User event payload: {}", event.getPayload());
    }

    @KafkaListener(topics = "payment-events", groupId = "events-service")
    public void consumePaymentEvent(Event event) {
        logger.info("Consumed payment event: {} - {}", event.getId(), event.getType());
        logger.info("Payment event payload: {}", event.getPayload());
    }
}

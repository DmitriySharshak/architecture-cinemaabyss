package com.cinemaabyss.events.service;

import com.cinemaabyss.events.model.Event;
import com.cinemaabyss.events.model.EventResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
public class EventProducerService {

    private static final Logger logger = LoggerFactory.getLogger(EventProducerService.class);

    @Autowired
    private KafkaTemplate<String, Event> kafkaTemplate;

    public Mono<EventResponse> publishMovieEvent(String topic, Map<String, Object> payload) {
        Event event = new Event(
                UUID.randomUUID().toString(),
                "movie",
                LocalDateTime.now(),
                payload
        );

        return publishEvent(topic, event);
    }

    public Mono<EventResponse> publishUserEvent(String topic, Map<String, Object> payload) {
        Event event = new Event(
                UUID.randomUUID().toString(),
                "user",
                LocalDateTime.now(),
                payload
        );

        return publishEvent(topic, event);
    }

    public Mono<EventResponse> publishPaymentEvent(String topic, Map<String, Object> payload) {
        Event event = new Event(
                UUID.randomUUID().toString(),
                "payment",
                LocalDateTime.now(),
                payload
        );

        return publishEvent(topic, event);
    }

    private Mono<EventResponse> publishEvent(String topic, Event event) {
        return Mono.create(sink -> {
            kafkaTemplate.send(topic, event.getId(), event)
                    .whenComplete((result, throwable) -> {
                        if (throwable != null) {
                            logger.error("Failed to publish event to topic {}: {}", topic, throwable.getMessage());
                            sink.error(throwable);
                        } else {
                            EventResponse response = new EventResponse(
                                    "success",
                                    result.getRecordMetadata().partition(),
                                    result.getRecordMetadata().offset(),
                                    event
                            );
                            logger.info("Event published successfully to topic {}: {}", topic, event.getId());
                            sink.success(response);
                        }
                    });
        });
    }
}

package com.cinemaabyss.events.controller;

import com.cinemaabyss.events.model.*;
import com.cinemaabyss.events.service.EventProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/events")
public class EventsController {

    @Autowired
    private EventProducerService eventProducerService;

    @GetMapping("/health")
    public Mono<Map<String, Boolean>> getHealth() {
        Map<String, Boolean> response = new HashMap<>();
        response.put("status", true);
        return Mono.just(response);
    }

    @PostMapping(value = "/movie", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EventResponse> createMovieEvent(@RequestBody MovieEvent movieEvent) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("movie_id", movieEvent.getMovieId());
        payload.put("title", movieEvent.getTitle());
        payload.put("action", movieEvent.getAction());
        payload.put("user_id", movieEvent.getUserId());
        payload.put("rating", movieEvent.getRating());
        payload.put("genres", movieEvent.getGenres());
        payload.put("description", movieEvent.getDescription());

        return eventProducerService.publishMovieEvent("movie-events", payload);
    }

    @PostMapping(value = "/user", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EventResponse> createUserEvent(@RequestBody UserEvent userEvent) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("user_id", userEvent.getUserId());
        payload.put("username", userEvent.getUsername());
        payload.put("email", userEvent.getEmail());
        payload.put("action", userEvent.getAction());
        payload.put("timestamp", userEvent.getTimestamp());

        return eventProducerService.publishUserEvent("user-events", payload);
    }

    @PostMapping(value = "/payment", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<EventResponse> createPaymentEvent(@RequestBody PaymentEvent paymentEvent) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("payment_id", paymentEvent.getPaymentId());
        payload.put("user_id", paymentEvent.getUserId());
        payload.put("amount", paymentEvent.getAmount());
        payload.put("status", paymentEvent.getStatus());
        payload.put("timestamp", paymentEvent.getTimestamp());
        payload.put("method_type", paymentEvent.getMethodType());

        return eventProducerService.publishPaymentEvent("payment-events", payload);
    }
}

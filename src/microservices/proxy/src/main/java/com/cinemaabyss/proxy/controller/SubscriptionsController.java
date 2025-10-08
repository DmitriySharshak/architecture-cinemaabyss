package com.cinemaabyss.proxy.controller;

import com.cinemaabyss.proxy.model.Subscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/subscriptions")
public class SubscriptionsController {

    @Autowired
    private WebClient monolithWebClient;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Subscription> getAllSubscriptions(@RequestParam(required = false) Integer userId) {
        String uri = "/api/subscriptions";
        if (userId != null) {
            uri += "?user_id=" + userId;
        }
        
        return monolithWebClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(Subscription.class);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Subscription> createSubscription(@RequestBody Subscription subscription) {
        return monolithWebClient
                .post()
                .uri("/api/subscriptions")
                .bodyValue(subscription)
                .retrieve()
                .bodyToMono(Subscription.class);
    }
}

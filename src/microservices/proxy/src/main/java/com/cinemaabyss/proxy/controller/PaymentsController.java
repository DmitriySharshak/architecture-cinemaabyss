package com.cinemaabyss.proxy.controller;

import com.cinemaabyss.proxy.model.Payment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/payments")
public class PaymentsController {

    @Autowired
    private WebClient monolithWebClient;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Payment> getAllPayments(@RequestParam(required = false) Integer userId) {
        String uri = "/api/payments";
        if (userId != null) {
            uri += "?user_id=" + userId;
        }
        
        return monolithWebClient
                .get()
                .uri(uri)
                .retrieve()
                .bodyToFlux(Payment.class);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Payment> createPayment(@RequestBody Payment payment) {
        return monolithWebClient
                .post()
                .uri("/api/payments")
                .bodyValue(payment)
                .retrieve()
                .bodyToMono(Payment.class);
    }
}

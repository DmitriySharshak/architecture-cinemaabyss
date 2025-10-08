package com.cinemaabyss.proxy.controller;

import com.cinemaabyss.proxy.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    @Autowired
    private WebClient monolithWebClient;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<User> getAllUsers() {
        return monolithWebClient
                .get()
                .uri("/api/users")
                .retrieve()
                .bodyToFlux(User.class);
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<User> createUser(@RequestBody User user) {
        return monolithWebClient
                .post()
                .uri("/api/users")
                .bodyValue(user)
                .retrieve()
                .bodyToMono(User.class);
    }
}

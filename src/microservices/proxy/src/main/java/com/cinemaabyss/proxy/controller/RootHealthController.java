package com.cinemaabyss.proxy.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class RootHealthController {

    @GetMapping("/health")
    public Mono<String> getHealth() {
        return Mono.just("Strangler Fig Proxy is healthy");
    }
}

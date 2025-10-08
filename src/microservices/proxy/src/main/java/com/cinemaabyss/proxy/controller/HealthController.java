package com.cinemaabyss.proxy.controller;

import com.cinemaabyss.proxy.service.StranglerFigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HealthController {

    @Autowired
    private WebClient moviesServiceWebClient;

    @Autowired
    private WebClient eventsServiceWebClient;
    
    @Autowired
    private StranglerFigService stranglerFigService;

    @GetMapping("/health")
    public Mono<String> getHealth() {
        return Mono.just("Strangler Fig Proxy is healthy");
    }

    @GetMapping("/api/health")
    public Mono<String> getApiHealth() {
        return Mono.just("Strangler Fig Proxy is healthy");
    }

    @GetMapping("/movies/health")
    public Mono<Object> getMoviesServiceHealth() {
        return moviesServiceWebClient
                .get()
                .uri("/api/movies/health")
                .retrieve()
                .bodyToMono(Object.class);
    }

    @GetMapping("/events/health")
    public Mono<Object> getEventsServiceHealth() {
        return eventsServiceWebClient
                .get()
                .uri("/api/events/health")
                .retrieve()
                .bodyToMono(Object.class);
    }
    
    @GetMapping("/traffic-stats")
    public Mono<Map<String, Object>> getTrafficStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("monolith_requests", stranglerFigService.getMonolithRequestCount());
        stats.put("microservice_requests", stranglerFigService.getMicroserviceRequestCount());
        stats.put("total_requests", stranglerFigService.getTotalRequestCount());
        stats.put("monolith_percentage", String.format("%.2f%%", stranglerFigService.getMonolithPercentage()));
        stats.put("microservice_percentage", String.format("%.2f%%", stranglerFigService.getMicroservicePercentage()));
        return Mono.just(stats);
    }
    
    @PostMapping("/traffic-stats/reset")
    public Mono<String> resetTrafficStats() {
        stranglerFigService.resetMetrics();
        return Mono.just("Traffic statistics reset successfully");
    }
    
    @GetMapping("/config")
    public Mono<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("monolith_url", "http://monolith:8080");
        config.put("movies_service_url", "http://movies-service:8081");
        config.put("events_service_url", "http://events-service:8082");
        config.put("gradual_migration", true);
        config.put("movies_migration_percent", "50% (from MOVIES_MIGRATION_PERCENT env var)");
        config.put("note", "To change migration percent, update MOVIES_MIGRATION_PERCENT in docker-compose.yml");
        return Mono.just(config);
    }
}

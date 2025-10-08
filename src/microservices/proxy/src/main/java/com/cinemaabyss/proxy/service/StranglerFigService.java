package com.cinemaabyss.proxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class StranglerFigService {

    private static final Logger logger = LoggerFactory.getLogger(StranglerFigService.class);

    @Autowired
    private MonolithMovieService monolithMovieService;

    @Autowired
    private MoviesMicroservice moviesMicroservice;

    private final Random random = new Random();
    
    // Метрики для отслеживания трафика
    private final AtomicLong monolithRequests = new AtomicLong(0);
    private final AtomicLong microserviceRequests = new AtomicLong(0);

    public <T> Mono<T> routeRequest(Mono<T> monolithResponse, Mono<T> microserviceResponse, int migrationPercent) {
        boolean useMicroservice = shouldUseMicroservice(migrationPercent);
        
        if (useMicroservice) {
            microserviceRequests.incrementAndGet();
            logger.info("🚀 Routing request to MICROSERVICE (migration: {}%, random: {})", 
                       migrationPercent, "N/A");
            return microserviceResponse;
        } else {
            monolithRequests.incrementAndGet();
            logger.info("🏛️  Routing request to MONOLITH (migration: {}%, random: {})", 
                       migrationPercent, "N/A");
            return monolithResponse;
        }
    }

    public <T> Flux<T> routeRequest(Flux<T> monolithResponse, Flux<T> microserviceResponse, int migrationPercent) {
        boolean useMicroservice = shouldUseMicroservice(migrationPercent);
        
        if (useMicroservice) {
            microserviceRequests.incrementAndGet();
            logger.info("🚀 Routing request to MICROSERVICE (migration: {}%, random: {})", 
                       migrationPercent, "N/A");
            return microserviceResponse;
        } else {
            monolithRequests.incrementAndGet();
            logger.info("🏛️  Routing request to MONOLITH (migration: {}%, random: {})", 
                       migrationPercent, "N/A");
            return monolithResponse;
        }
    }

    public boolean shouldUseMicroservice(int migrationPercent) {
        if (migrationPercent <= 0) {
            logger.debug("Migration percent {}% -> Using MONOLITH", migrationPercent);
            return false;
        } else if (migrationPercent >= 100) {
            logger.debug("Migration percent {}% -> Using MICROSERVICE", migrationPercent);
            return true;
        } else {
            int randomValue = random.nextInt(100);
            boolean useMicroservice = randomValue < migrationPercent;
            logger.debug("Migration percent {}%, random value {} -> Using {}", 
                        migrationPercent, randomValue, useMicroservice ? "MICROSERVICE" : "MONOLITH");
            return useMicroservice;
        }
    }
    
    // Методы для получения статистики
    public long getMonolithRequestCount() {
        return monolithRequests.get();
    }
    
    public long getMicroserviceRequestCount() {
        return microserviceRequests.get();
    }
    
    public long getTotalRequestCount() {
        return monolithRequests.get() + microserviceRequests.get();
    }
    
    public double getMonolithPercentage() {
        long total = getTotalRequestCount();
        return total > 0 ? (double) monolithRequests.get() / total * 100 : 0;
    }
    
    public double getMicroservicePercentage() {
        long total = getTotalRequestCount();
        return total > 0 ? (double) microserviceRequests.get() / total * 100 : 0;
    }
    
    public void resetMetrics() {
        monolithRequests.set(0);
        microserviceRequests.set(0);
        logger.info("📊 Traffic metrics reset");
    }
}

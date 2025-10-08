package com.cinemaabyss.proxy.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@ConfigurationProperties(prefix = "proxy")
public class ProxyConfiguration {

    @Value("${MONOLITH_URL:http://localhost:8080}")
    private String monolithUrl;
    
    @Value("${MOVIES_SERVICE_URL:http://localhost:8081}")
    private String moviesServiceUrl;
    
    @Value("${EVENTS_SERVICE_URL:http://localhost:8082}")
    private String eventsServiceUrl;
    
    @Value("${GRADUAL_MIGRATION:true}")
    private boolean gradualMigration;
    
    @Value("${MOVIES_MIGRATION_PERCENT:50}")
    private int moviesMigrationPercent;

    @Bean
    public WebClient monolithWebClient() {
        return WebClient.builder()
                .baseUrl(monolithUrl)
                .build();
    }

    @Bean
    public WebClient moviesServiceWebClient() {
        return WebClient.builder()
                .baseUrl(moviesServiceUrl)
                .build();
    }

    @Bean
    public WebClient eventsServiceWebClient() {
        return WebClient.builder()
                .baseUrl(eventsServiceUrl)
                .build();
    }

    // Getters and setters
    public String getMonolithUrl() {
        return monolithUrl;
    }

    public void setMonolithUrl(String monolithUrl) {
        this.monolithUrl = monolithUrl;
    }

    public String getMoviesServiceUrl() {
        return moviesServiceUrl;
    }

    public void setMoviesServiceUrl(String moviesServiceUrl) {
        this.moviesServiceUrl = moviesServiceUrl;
    }

    public String getEventsServiceUrl() {
        return eventsServiceUrl;
    }

    public void setEventsServiceUrl(String eventsServiceUrl) {
        this.eventsServiceUrl = eventsServiceUrl;
    }

    public boolean isGradualMigration() {
        return gradualMigration;
    }

    public void setGradualMigration(boolean gradualMigration) {
        this.gradualMigration = gradualMigration;
    }

    public int getMoviesMigrationPercent() {
        return moviesMigrationPercent;
    }

    public void setMoviesMigrationPercent(int moviesMigrationPercent) {
        this.moviesMigrationPercent = moviesMigrationPercent;
    }
}

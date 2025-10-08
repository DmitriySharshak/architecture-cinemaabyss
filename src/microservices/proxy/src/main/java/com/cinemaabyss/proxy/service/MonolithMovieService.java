package com.cinemaabyss.proxy.service;

import com.cinemaabyss.proxy.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MonolithMovieService {

    @Autowired
    private WebClient monolithWebClient;

    public Flux<Movie> getAllMovies() {
        return monolithWebClient
                .get()
                .uri("/api/movies")
                .retrieve()
                .bodyToFlux(Movie.class);
    }

    public Mono<Movie> createMovie(Movie movie) {
        return monolithWebClient
                .post()
                .uri("/api/movies")
                .bodyValue(movie)
                .retrieve()
                .bodyToMono(Movie.class);
    }

    public Mono<Movie> getMovieById(Integer id) {
        return monolithWebClient
                .get()
                .uri("/api/movies?id={id}", id)
                .retrieve()
                .bodyToMono(Movie.class);
    }
}

package com.cinemaabyss.proxy.service;

import com.cinemaabyss.proxy.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoviesMicroservice {

    @Autowired
    private WebClient moviesServiceWebClient;

    public Flux<Movie> getAllMovies() {
        return moviesServiceWebClient
                .get()
                .uri("/api/movies")
                .retrieve()
                .bodyToFlux(Movie.class);
    }

    public Mono<Movie> createMovie(Movie movie) {
        return moviesServiceWebClient
                .post()
                .uri("/api/movies")
                .bodyValue(movie)
                .retrieve()
                .bodyToMono(Movie.class);
    }

    public Mono<Movie> getMovieById(Integer id) {
        return moviesServiceWebClient
                .get()
                .uri("/api/movies?id={id}", id)
                .retrieve()
                .bodyToMono(Movie.class);
    }
}

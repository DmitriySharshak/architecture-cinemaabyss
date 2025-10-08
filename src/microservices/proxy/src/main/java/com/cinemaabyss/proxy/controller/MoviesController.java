package com.cinemaabyss.proxy.controller;

import com.cinemaabyss.proxy.config.ProxyConfiguration;
import com.cinemaabyss.proxy.model.Movie;
import com.cinemaabyss.proxy.service.MonolithMovieService;
import com.cinemaabyss.proxy.service.MoviesMicroservice;
import com.cinemaabyss.proxy.service.StranglerFigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/movies")
public class MoviesController {

    @Autowired
    private MonolithMovieService monolithMovieService;

    @Autowired
    private MoviesMicroservice moviesMicroservice;

    @Autowired
    private StranglerFigService stranglerFigService;

    @Autowired
    private ProxyConfiguration config;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<Movie> getAllMovies() {
        if (config.isGradualMigration()) {
            return stranglerFigService.routeRequest(
                    monolithMovieService.getAllMovies(),
                    moviesMicroservice.getAllMovies(),
                    config.getMoviesMigrationPercent()
            );
        } else {
            return monolithMovieService.getAllMovies();
        }
    }

    @PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Movie> createMovie(@RequestBody Movie movie) {
        if (config.isGradualMigration()) {
            return stranglerFigService.routeRequest(
                    monolithMovieService.createMovie(movie),
                    moviesMicroservice.createMovie(movie),
                    config.getMoviesMigrationPercent()
            );
        } else {
            return monolithMovieService.createMovie(movie);
        }
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Movie> getMovieById(@PathVariable Integer id) {
        if (config.isGradualMigration()) {
            return stranglerFigService.routeRequest(
                    monolithMovieService.getMovieById(id),
                    moviesMicroservice.getMovieById(id),
                    config.getMoviesMigrationPercent()
            );
        } else {
            return monolithMovieService.getMovieById(id);
        }
    }
}

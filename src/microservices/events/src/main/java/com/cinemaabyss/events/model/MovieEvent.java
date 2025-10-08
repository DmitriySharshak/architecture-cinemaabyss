package com.cinemaabyss.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class MovieEvent {
    private Integer movieId;
    private String title;
    private String action;
    private Integer userId;
    private Double rating;
    private List<String> genres;
    private String description;

    public MovieEvent() {}

    public MovieEvent(Integer movieId, String title, String action) {
        this.movieId = movieId;
        this.title = title;
        this.action = action;
    }

    @JsonProperty("movie_id")
    public Integer getMovieId() {
        return movieId;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("action")
    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    @JsonProperty("user_id")
    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @JsonProperty("rating")
    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @JsonProperty("genres")
    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

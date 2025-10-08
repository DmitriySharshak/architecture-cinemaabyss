package com.cinemaabyss.proxy.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public class Movie {
    private Integer id;
    private String title;
    private String description;
    private List<String> genres;
    private Double rating;

    public Movie() {}

    public Movie(Integer id, String title, String description, List<String> genres, Double rating) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genres = genres;
        this.rating = rating;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("genres")
    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    @JsonProperty("rating")
    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }
}

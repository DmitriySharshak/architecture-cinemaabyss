package com.cinemaabyss.proxy.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class User {
    private Integer id;
    private String username;
    private String email;

    public User() {}

    public User(Integer id, String username, String email) {
        this.id = id;
        this.username = username;
        this.email = email;
    }

    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

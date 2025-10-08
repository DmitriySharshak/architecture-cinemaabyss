package com.cinemaabyss.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Map;

public class Event {
    private String id;
    private String type;
    private LocalDateTime timestamp;
    private Map<String, Object> payload;

    public Event() {}

    public Event(String id, String type, LocalDateTime timestamp, Map<String, Object> payload) {
        this.id = id;
        this.type = type;
        this.timestamp = timestamp;
        this.payload = payload;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("timestamp")
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @JsonProperty("payload")
    public Map<String, Object> getPayload() {
        return payload;
    }

    public void setPayload(Map<String, Object> payload) {
        this.payload = payload;
    }
}

package com.cinemaabyss.events.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventResponse {
    private String status;
    private Integer partition;
    private Long offset;
    private Event event;

    public EventResponse() {}

    public EventResponse(String status, Integer partition, Long offset, Event event) {
        this.status = status;
        this.partition = partition;
        this.offset = offset;
        this.event = event;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("partition")
    public Integer getPartition() {
        return partition;
    }

    public void setPartition(Integer partition) {
        this.partition = partition;
    }

    @JsonProperty("offset")
    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    @JsonProperty("event")
    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
}

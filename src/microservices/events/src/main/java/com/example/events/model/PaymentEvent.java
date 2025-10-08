package com.example.events.model;

public record PaymentEvent(String paymentId, String userId, String status, Double amount) { }


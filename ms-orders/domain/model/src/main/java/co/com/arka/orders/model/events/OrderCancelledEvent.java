package co.com.arka.orders.model.events;

public record OrderCancelledEvent(String orderId, String reason) {}


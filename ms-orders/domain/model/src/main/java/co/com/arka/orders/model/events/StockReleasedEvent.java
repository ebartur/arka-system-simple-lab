package co.com.arka.orders.model.events;

public record StockReleasedEvent(String orderId, String reason) {
}
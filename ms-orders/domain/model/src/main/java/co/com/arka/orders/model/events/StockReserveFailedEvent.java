package co.com.arka.orders.model.events;

public record StockReserveFailedEvent(String orderId, String reason) {
}
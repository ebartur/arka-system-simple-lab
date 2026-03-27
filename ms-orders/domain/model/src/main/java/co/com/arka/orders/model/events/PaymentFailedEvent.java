package co.com.arka.orders.model.events;

public record PaymentFailedEvent(
        String orderId,
        String sku,
        Integer quantity,
        String reason
) {
}
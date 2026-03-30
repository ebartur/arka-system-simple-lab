package co.com.arka.inventory.model.events;

public record PaymentFailedEvent(
        String orderId,
        String sku,
        Integer quantity,
        String reason) {
}

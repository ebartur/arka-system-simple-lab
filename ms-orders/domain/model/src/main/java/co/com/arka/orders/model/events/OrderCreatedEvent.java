package co.com.arka.orders.model.events;

public record OrderCreatedEvent(
        String orderId,
        String sku,
        Integer quantity,
        Double amount
) {
}

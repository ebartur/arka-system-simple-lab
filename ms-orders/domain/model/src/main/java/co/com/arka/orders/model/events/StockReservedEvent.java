package co.com.arka.orders.model.events;

public record StockReservedEvent(
        String orderId,
        String sku,
        Integer quantity
) {
}
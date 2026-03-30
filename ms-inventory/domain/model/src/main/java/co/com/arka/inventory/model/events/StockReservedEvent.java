package co.com.arka.inventory.model.events;

public record StockReservedEvent(
        String orderId,
        String sku,
        Integer quantity
) {
}

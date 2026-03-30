package co.com.arka.inventory.model.events;

public record StockReserveFailedEvent(
        String orderId, String reason
) {
}

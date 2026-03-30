package co.com.arka.inventory.model.events;

public record StockReleasedEvent(
        String orderId, String reason
) {
}

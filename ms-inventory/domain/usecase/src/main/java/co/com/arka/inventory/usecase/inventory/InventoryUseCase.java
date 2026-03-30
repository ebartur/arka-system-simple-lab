package co.com.arka.inventory.usecase.inventory;

import co.com.arka.inventory.model.events.OrderCreatedEvent;
import co.com.arka.inventory.model.events.StockReleasedEvent;
import co.com.arka.inventory.model.events.StockReserveFailedEvent;
import co.com.arka.inventory.model.events.StockReservedEvent;
import co.com.arka.inventory.model.events.gateways.EventsGateway;
import co.com.arka.inventory.model.product.Product;
import co.com.arka.inventory.model.product.gateways.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.logging.Level;

@Log
@RequiredArgsConstructor
public class InventoryUseCase {
    private final ProductRepository productRepository;
    private final EventsGateway<StockReservedEvent> stockReservedEventsGateway;
    private final EventsGateway<StockReleasedEvent> stockReleasedEventsGateway;
    private final EventsGateway<StockReserveFailedEvent> stockReserveFailedEventsGateway;

    public Mono<Void> reserveStock(OrderCreatedEvent event) {
        log.log(Level.INFO, "Reserving stock for order: {0}, sku: {1}, qty: {2}",
                new Object[]{event.orderId(), event.sku(), event.quantity()});

        return productRepository.findBySku(event.sku())
                .flatMap(product -> {
                    if (product.getStock() >= event.quantity()) {
                        product.setStock(product.getStock() - event.quantity());
                        return productRepository.save(product)
                                .flatMap(saved -> {
                                    var reserved = new StockReservedEvent(event.orderId(), event.sku(), event.quantity());
                                    log.log(Level.INFO, "Stock reserved successfully for order: {0}", event.orderId());
                                    return stockReservedEventsGateway.emit(reserved);
                                });
                    } else {
                        var failed = new StockReserveFailedEvent(event.orderId(),
                                "Insufficient stock for SKU: " + event.sku()
                                        + ". Available: " + product.getStock()
                                        + ", Requested: " + event.quantity());
                        log.log(Level.WARNING, "Stock reservation failed for order: {0}", event.orderId());
                        return stockReserveFailedEventsGateway.emit(failed);
                    }
                })
                .switchIfEmpty(Mono.defer(() -> {
                    var failed = new StockReserveFailedEvent(event.orderId(),
                            "Product not found for SKU: " + event.sku());
                    log.log(Level.WARNING, "Product not found for SKU: {0}", event.sku());
                    return stockReserveFailedEventsGateway.emit(failed);
                }));
    }

    public Mono<Void> releaseStock(String orderId, String sku, Integer quantity, String reason) {
        if (sku == null || quantity == null || quantity <= 0) {
            log.log(Level.WARNING, "Cannot release stock for order {0}: invalid reservation data (sku={1}, quantity={2})",
                    new Object[]{orderId, sku, quantity});
            var released = new StockReleasedEvent(orderId, reason + " (invalid reservation data)");
            return stockReleasedEventsGateway.emit(released);
        }

        log.log(Level.INFO, "Releasing stock for order: {0}, sku: {1}, qty: {2}",
                new Object[]{orderId, sku, quantity});

        return productRepository.findBySku(sku)
                .flatMap(product -> {
                    product.setStock(product.getStock() + quantity);
                    return productRepository.save(product)
                            .flatMap(saved -> {
                                var released = new StockReleasedEvent(orderId, reason);
                                log.log(Level.INFO, "Stock released for order: {0}", orderId);
                                return stockReleasedEventsGateway.emit(released);
                            });
                })
                .switchIfEmpty(Mono.defer(() -> {
                    log.log(Level.WARNING, "Cannot release stock: product not found for SKU: {0}", sku);
                    var released = new StockReleasedEvent(orderId, reason + " (product not found)");
                    return stockReleasedEventsGateway.emit(released);
                }));
    }


    public Mono<Product> getProduct(String sku) {
        return productRepository.findBySku(sku);
    }

    public Flux<Product> getAllProducts() {
        return productRepository.findAll();
    }
}

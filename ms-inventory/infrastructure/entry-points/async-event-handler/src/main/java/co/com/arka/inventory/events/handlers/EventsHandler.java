package co.com.arka.inventory.events.handlers;

import co.com.arka.inventory.model.events.OrderCreatedEvent;
import co.com.arka.inventory.model.events.PaymentFailedEvent;
import co.com.arka.inventory.usecase.inventory.InventoryUseCase;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.async.impl.config.annotations.EnableEventListeners;
import reactor.core.publisher.Mono;
import lombok.extern.java.Log;

import java.util.Optional;
import java.util.logging.Level;
import io.cloudevents.CloudEvent;
import tools.jackson.databind.ObjectMapper;

@Log
@RequiredArgsConstructor
@EnableEventListeners
public class EventsHandler {
    private final InventoryUseCase inventoryUseCase;
    private final ObjectMapper objectMapper;

    public Mono<Void> handleOrderCreatedEvent(CloudEvent event) {
        Optional<OrderCreatedEvent> orderCreatedEvent = deserialize(event, OrderCreatedEvent.class);

        return Mono.justOrEmpty(orderCreatedEvent)
                .doOnNext(e -> log.log(Level.INFO, "Event received: {0} -> {1}", new Object[]{event.getType(), e}))
                .flatMap(inventoryUseCase::reserveStock)
                .doOnSuccess(v -> log.info("Stock reservation processed for event: " + event.getId()))
                .then();
    }

    public Mono<Void> handlePaymentFailedEvent(CloudEvent event) {
        Optional<PaymentFailedEvent> paymentFailedEvent = deserialize(event, PaymentFailedEvent.class);

        return Mono.justOrEmpty(paymentFailedEvent)
                .doOnNext(e -> log.log(Level.INFO, "Event received: {0} -> {1}", new Object[]{event.getType(), e}))
                .flatMap(e -> inventoryUseCase.releaseStock(e.orderId(), e.sku(), e.quantity(), e.reason()))
                .then();
    }


    private <T> Optional<T> deserialize(CloudEvent event, Class<T> clazz) {
        if (event == null || event.getData() == null) {
            log.warning("Received event with empty data");
            return Optional.empty();
        }

        try {
            return Optional.of(objectMapper.readValue(event.getData().toBytes(), clazz));
        } catch (Exception e) {
            log.log(Level.SEVERE, "Failed to deserialize event data to " + clazz.getSimpleName(), e);
            throw new RuntimeException("Failed to map event data", e);
        }
    }
}

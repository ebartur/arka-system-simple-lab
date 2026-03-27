package co.com.arka.orders.events.handlers;

import co.com.arka.orders.model.events.StockReleasedEvent;
import co.com.arka.orders.model.events.StockReserveFailedEvent;
import co.com.arka.orders.model.events.StockReservedEvent;
import co.com.arka.orders.usecase.order.OrderUseCase;
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
    private final OrderUseCase orderUseCase;
    private final ObjectMapper objectMapper;

    public Mono<Void> handleStockFailedEvent(CloudEvent event) {
        Optional<StockReserveFailedEvent> stockFailedEvent = deserialize(event, StockReserveFailedEvent.class);

        return Mono.justOrEmpty(stockFailedEvent)
                .doOnNext(st -> log.log(Level.INFO, "Event received: {0} -> {1}", new Object[]{event.getType(), st}))
                .flatMap(st -> orderUseCase.cancelOrder(st.orderId(), "Out of stock: " + st.reason()))
                .doOnNext(order -> log.info("Order: " + order.getId() + " cancelled due to stock failure"))
                .then();
    }

    public Mono<Void> handleStockReleasedEvent(CloudEvent event) {
        Optional<StockReleasedEvent> stockReleasedEvent = deserialize(event, StockReleasedEvent.class);

        return Mono.justOrEmpty(stockReleasedEvent)
                .doOnNext(st -> log.log(Level.INFO, "Event received: {0} -> {1}", new Object[]{event.getType(), st}))
                .flatMap(st -> orderUseCase.cancelOrder(st.orderId(), "Failed: " + st.reason()))
                .doOnNext(order -> log.info("Order: " + order.getId() + " cancelled"))
                .then();
    }

    public Mono<Void> handleStockReservedEvent(CloudEvent event) {
        Optional<StockReservedEvent> stockReservedEvent = deserialize(event, StockReservedEvent.class);

        return Mono.justOrEmpty(stockReservedEvent)
                .doOnNext(st -> log.log(Level.INFO, "Event received: {0} -> {1}", new Object[]{event.getType(), st}))
                .flatMap(st -> orderUseCase.processPaymentForOrder(st.orderId()))
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

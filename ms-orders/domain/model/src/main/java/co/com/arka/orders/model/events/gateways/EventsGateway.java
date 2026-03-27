package co.com.arka.orders.model.events.gateways;

import reactor.core.publisher.Mono;

public interface EventsGateway<T> {
    Mono<Void> emit(T event);
}

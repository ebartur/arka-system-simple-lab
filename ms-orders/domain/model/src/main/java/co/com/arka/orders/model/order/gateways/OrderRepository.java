package co.com.arka.orders.model.order.gateways;

import co.com.arka.orders.model.order.Order;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface OrderRepository {
    Mono<Order> save(Order order);
    Mono<Order> findById(String id);
    Flux<Order> findAll();
}

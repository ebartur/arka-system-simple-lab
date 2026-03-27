package co.com.arka.orders.api;

import co.com.arka.orders.model.order.Order;
import co.com.arka.orders.usecase.order.OrderUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class Handler {
    private final OrderUseCase orderUseCase;

    public Mono<ServerResponse> healthCheck(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue(
                Map.of(
                        "service", "ms-orders",
                        "status", "UP",
                        "timestamp", LocalDateTime.now().toString()
                )
        );
    }

    public Mono<ServerResponse> createOrder(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(Order.class)
                .flatMap(orderUseCase::createOrder)
                .flatMap(order -> ServerResponse.ok().bodyValue(order));
    }

    public Mono<ServerResponse> getOrder(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return orderUseCase.getOrder(id)
                .flatMap(order -> ServerResponse.ok().bodyValue(order))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getAllOrders(ServerRequest serverRequest) {
        return ServerResponse.ok().body(orderUseCase.getAllOrders(), Order.class);
    }
}

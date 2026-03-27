package co.com.arka.orders.usecase.order;

import co.com.arka.orders.model.events.OrderCancelledEvent;
import co.com.arka.orders.model.events.OrderConfirmedEvent;
import co.com.arka.orders.model.events.OrderCreatedEvent;
import co.com.arka.orders.model.events.PaymentFailedEvent;
import co.com.arka.orders.model.events.gateways.EventsGateway;
import co.com.arka.orders.model.order.Order;
import co.com.arka.orders.model.order.gateways.OrderRepository;
import co.com.arka.orders.model.payment.gateways.PaymentGateway;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class OrderUseCase {
    private final OrderRepository orderRepository;
    private final PaymentGateway paymentGateway;
    private final EventsGateway<OrderCreatedEvent> orderCreatedEventGateway;
    private final EventsGateway<OrderConfirmedEvent> orderConfirmedEventEventsGateway;
    private final EventsGateway<OrderCancelledEvent> orderCancelledEventEventsGateway;
    private final EventsGateway<PaymentFailedEvent> paymentFailedEventEventsGateway;

    public Mono<Order> createOrder(Order order) {
        order.setStatus("PENDING");
        order.setTotalAmount(order.getUnitPrice() * order.getQuantity());
        return orderRepository.save(order)
                .flatMap(orderSaved -> {
                    var orderCreatedEvent = new OrderCreatedEvent(
                        orderSaved.getId(),
                        orderSaved.getSku(),
                        orderSaved.getQuantity(),
                        orderSaved.getTotalAmount()
                    );
                    return orderCreatedEventGateway.emit(orderCreatedEvent)
                            .thenReturn(orderSaved);
                });
    }

    public Mono<Order> cancelOrder(String orderId, String reason) {
        return orderRepository.findById(orderId)
                .flatMap(order -> {
                    order.setStatus("CANCELLED");
                    return orderRepository.save(order)
                            .flatMap(orderCancelled -> {
                                var event = new OrderCancelledEvent(orderCancelled.getId(), reason);
                                return orderCancelledEventEventsGateway.emit(event)
                                        .thenReturn(orderCancelled);
                            });
                });
    }

    public Mono<Order> confirmOrder(String orderId) {
        return orderRepository.findById(orderId)
                .flatMap(order -> {
                    order.setStatus("CONFIRMED");
                    return orderRepository.save(order)
                            .flatMap(orderCancelled -> {
                                var event = new OrderConfirmedEvent(orderCancelled.getId());
                                return orderConfirmedEventEventsGateway.emit(event)
                                        .thenReturn(orderCancelled);
                            });
                });
    }


    public Mono<Void> processPaymentForOrder(String orderId) {
        return orderRepository.findById(orderId)
                .flatMap(order -> paymentGateway.processPayment(order.getId(), order.getTotalAmount())
                        .flatMap(approved -> approved
                                ? confirmOrder(order.getId()).then()
                                : publishPaymentFailed(order, "Payment rejected by provider"))
                        .onErrorResume(ex -> publishPaymentFailed(order, "Payment gateway error: " + ex.getMessage())));
    }

    private Mono<Void> publishPaymentFailed(Order order, String reason) {
        var failed = new PaymentFailedEvent(
                order.getId(),
                order.getSku(),
                order.getQuantity(),
                reason
        );
        return paymentFailedEventEventsGateway.emit(failed);
    }

    public Mono<Order> getOrder(String id) {
        return orderRepository.findById(id);
    }

    public Flux<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}

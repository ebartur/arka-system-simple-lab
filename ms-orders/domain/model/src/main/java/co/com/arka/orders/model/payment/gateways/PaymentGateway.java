package co.com.arka.orders.model.payment.gateways;

import reactor.core.publisher.Mono;

public interface PaymentGateway {
    Mono<Boolean> processPayment(String orderId, Double amount);
}

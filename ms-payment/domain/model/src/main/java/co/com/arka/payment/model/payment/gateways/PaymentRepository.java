package co.com.arka.payment.model.payment.gateways;

import co.com.arka.payment.model.payment.Payment;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface PaymentRepository {
    Mono<Payment> save(Payment payment);
    Mono<Payment> findById(String id);
    Flux<Payment> findAll();
}

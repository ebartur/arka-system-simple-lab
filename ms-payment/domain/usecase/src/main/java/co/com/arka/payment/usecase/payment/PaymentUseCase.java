package co.com.arka.payment.usecase.payment;

import co.com.arka.payment.model.payment.Payment;
import co.com.arka.payment.model.payment.gateways.PaymentRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@RequiredArgsConstructor
public class PaymentUseCase {
    private static final double FAILURE_PROBABILITY = 0.30;
    private final PaymentRepository paymentRepository;

    public Mono<Payment> processPayment(String orderId, Double amount, String paymentMethod) {
        boolean approved = ThreadLocalRandom.current().nextDouble() >= FAILURE_PROBABILITY;
        String status = approved ? "PROCESSED" : "FAILED";

        Payment payment = Payment.builder()
                .orderId(orderId)
                .amount(amount)
                .status(status)
                .paymentMethod(paymentMethod)
                .processedAt(LocalDateTime.now())
                .build();

        return paymentRepository.save(payment);
    }

    public Mono<Payment> getPayment(String id) {
        return paymentRepository.findById(id);
    }

    public Flux<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}

package co.com.arka.orders.consumer;

import co.com.arka.orders.model.payment.gateways.PaymentGateway;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestConsumer implements PaymentGateway {
    private final WebClient client;
    @Value("${payment.process-path}")
    private String processPath;

    @CircuitBreaker(name = "payment") // This name should match with settings name in application.yaml
    public Mono<Boolean> processPayment(String orderId, Double amount) {
        var request = PaymentRequest.builder()
                .orderId(orderId)
                .amount(amount).build();
        return client
                .post()
                .uri(processPath)
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .map(response -> true)
                .doOnError(error -> log.warn("Payment request failed for order {}: {}", orderId, error.getMessage()))
                .onErrorReturn(false);
    }
}

package co.com.arka.payment.api;

import co.com.arka.payment.model.payment.Payment;
import co.com.arka.payment.usecase.payment.PaymentUseCase;
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
    private  final PaymentUseCase paymentUseCase;

    public Mono<ServerResponse> healthCheck(ServerRequest serverRequest) {
        return ServerResponse.ok().bodyValue(Map.of(
                "service", "ms-payment",
                "status", "UP",
                "timestamp", LocalDateTime.now().toString()
        ));
    }
    public Mono<ServerResponse> processPayment(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(ProcessPaymentRequest.class)
                .flatMap(request -> paymentUseCase.processPayment(request.orderId(), request.amount(), "DEFAULT"))
                .flatMap(payment -> {
                    if ("PROCESSED".equals(payment.getStatus())) {
                        return ServerResponse.ok().bodyValue(payment);
                    }
                    return ServerResponse.status(402).bodyValue(payment);
                });
    }

    public Mono<ServerResponse> getPayment(ServerRequest serverRequest) {
        String id = serverRequest.pathVariable("id");
        return paymentUseCase.getPayment(id)
                .flatMap(payment -> ServerResponse.ok().bodyValue(payment))
                .switchIfEmpty(ServerResponse.notFound().build());
    }

    public Mono<ServerResponse> getAllPayments(ServerRequest serverRequest) {
        return ServerResponse.ok().body(paymentUseCase.getAllPayments(), Payment.class);
    }

    public record ProcessPaymentRequest(
            String orderId,
            Double amount
    ) {
    }
}

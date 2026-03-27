package co.com.arka.payment.model.payment;
import lombok.*;
//import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Payment {
    private String id;
    private String orderId;
    private Double amount;
    private String status; // PROCESSED, FAILED
    private String paymentMethod;
    private java.time.LocalDateTime processedAt;
}

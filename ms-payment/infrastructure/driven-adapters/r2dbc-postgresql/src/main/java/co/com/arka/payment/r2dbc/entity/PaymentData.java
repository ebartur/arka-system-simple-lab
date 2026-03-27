package co.com.arka.payment.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table("payments")
public class PaymentData {
    @Id
    private String id;
    private String orderId;
    private Double amount;
    private String status;
    private String paymentMethod;
    private java.time.LocalDateTime processedAt;
}

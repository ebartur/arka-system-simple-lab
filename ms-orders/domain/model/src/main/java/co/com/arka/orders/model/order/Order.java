package co.com.arka.orders.model.order;
import lombok.*;
//import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Order {
    private String id;
    private String customerId;
    private String sku;
    private Integer quantity;
    private Double unitPrice;
    private Double totalAmount;
    @Builder.Default
    private String status = "PENDING";
}
package co.com.arka.orders.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class OrderData {
    @Id
    private String id;
    private String customerId;
    private String sku;
    private Integer quantity;
    private Double totalAmount;
    private String status;
}

package co.com.arka.inventory.r2dbc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("products")
public class ProductData {
    @Id
    private String id;
    private String sku;
    private String name;
    private Double price;
    private Integer stock;
    private String category;
}

package co.com.arka.inventory.model.product;
import lombok.*;
//import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Product {
    private String id;
    private String sku;
    private String name;
    private Double price;
    private Integer stock;
    private String category;
}

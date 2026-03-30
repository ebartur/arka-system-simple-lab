package co.com.arka.inventory.r2dbc;

import co.com.arka.inventory.model.product.Product;
import co.com.arka.inventory.model.product.gateways.ProductRepository;
import co.com.arka.inventory.r2dbc.entity.ProductData;
import co.com.arka.inventory.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public class ProductReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    Product/* change for domain model */,
    ProductData/* change for adapter model */,
    String,
    ProductReactiveRepository
> implements ProductRepository {
    public ProductReactiveRepositoryAdapter(ProductReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Product.class/* change for domain model */));
    }

    @Override
    public Mono<Product> findBySku(String sku) {
        return repository.findBySku(sku).map(this::toEntity);
    }

}

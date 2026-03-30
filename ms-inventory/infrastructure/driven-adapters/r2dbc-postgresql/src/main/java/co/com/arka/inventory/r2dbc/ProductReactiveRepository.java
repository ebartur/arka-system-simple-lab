package co.com.arka.inventory.r2dbc;

import co.com.arka.inventory.r2dbc.entity.ProductData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

// TODO: This file is just an example, you should delete or modify it
public interface ProductReactiveRepository extends ReactiveCrudRepository<ProductData, String>, ReactiveQueryByExampleExecutor<ProductData> {
    Mono<ProductData> findBySku(String sku);
}

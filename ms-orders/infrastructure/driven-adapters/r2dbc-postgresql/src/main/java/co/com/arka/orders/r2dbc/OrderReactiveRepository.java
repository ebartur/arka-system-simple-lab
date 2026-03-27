package co.com.arka.orders.r2dbc;

import co.com.arka.orders.r2dbc.entity.OrderData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

// TODO: This file is just an example, you should delete or modify it
public interface OrderReactiveRepository extends ReactiveCrudRepository<OrderData, String>, ReactiveQueryByExampleExecutor<OrderData> {

}

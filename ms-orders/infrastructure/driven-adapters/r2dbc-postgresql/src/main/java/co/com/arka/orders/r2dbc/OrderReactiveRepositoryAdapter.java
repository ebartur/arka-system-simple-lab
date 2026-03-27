package co.com.arka.orders.r2dbc;

import co.com.arka.orders.model.order.Order;
import co.com.arka.orders.model.order.gateways.OrderRepository;
import co.com.arka.orders.r2dbc.entity.OrderData;
import co.com.arka.orders.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class OrderReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    Order,
    OrderData/* change for adapter model */,
    String,
    OrderReactiveRepository
> implements OrderRepository {
    public OrderReactiveRepositoryAdapter(OrderReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository,
        orderData -> mapper.map(orderData, Order.class),
        order -> OrderData.builder()
                .id(order.getId())
                .customerId(order.getCustomerId())
                .sku(order.getSku())
                .quantity(order.getQuantity())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .build());
    }

}

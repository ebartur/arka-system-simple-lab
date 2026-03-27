package co.com.arka.payment.r2dbc;

import co.com.arka.payment.r2dbc.entity.PaymentData;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

// TODO: This file is just an example, you should delete or modify it
public interface PaymentReactiveRepository extends ReactiveCrudRepository<PaymentData, String>, ReactiveQueryByExampleExecutor<PaymentData> {

}

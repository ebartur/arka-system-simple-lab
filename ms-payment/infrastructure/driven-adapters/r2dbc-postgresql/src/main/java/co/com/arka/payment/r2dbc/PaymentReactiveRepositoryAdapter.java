package co.com.arka.payment.r2dbc;

import co.com.arka.payment.model.payment.Payment;
import co.com.arka.payment.model.payment.gateways.PaymentRepository;
import co.com.arka.payment.r2dbc.entity.PaymentData;
import co.com.arka.payment.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    Payment/* change for domain model */,
    PaymentData/* change for adapter model */,
    String,
    PaymentReactiveRepository
> implements PaymentRepository {
    public PaymentReactiveRepositoryAdapter(PaymentReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Payment.class/* change for domain model */));
    }

}

package co.com.arka.inventory.events;
import co.com.arka.inventory.events.config.KafkaBrokerSecretConsumer;
import co.com.arka.inventory.events.handlers.EventsHandler;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.async.api.HandlerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HandlerRegistryConfiguration {
    private final KafkaBrokerSecretConsumer kafkaBrokerSecretConsumer;

    // see more at: https://reactivecommons.org/reactive-commons-java/#_handlerregistry_2
    @Bean
    public HandlerRegistry handlerRegistry(EventsHandler events) {
         return HandlerRegistry.register()
                 .listenCloudEvent(kafkaBrokerSecretConsumer.topics().orderCreated(), events::handleOrderCreatedEvent)
                 .listenCloudEvent(kafkaBrokerSecretConsumer.topics().paymentFailed(), events::handlePaymentFailedEvent);
    }
}

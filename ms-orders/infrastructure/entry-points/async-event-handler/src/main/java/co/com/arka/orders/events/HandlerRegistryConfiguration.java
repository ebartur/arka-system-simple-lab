package co.com.arka.orders.events;
import co.com.arka.orders.events.config.KafkaBrokerSecretConsumer;
import co.com.arka.orders.events.handlers.EventsHandler;
import lombok.RequiredArgsConstructor;
import org.reactivecommons.async.api.HandlerRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class HandlerRegistryConfiguration {

    private final KafkaBrokerSecretConsumer  kafkaBrokerSecretConsumer;

    // see more at: https://reactivecommons.org/reactive-commons-java/#_handlerregistry_2
    @Bean
    public HandlerRegistry handlerRegistry(EventsHandler events) {
         return HandlerRegistry.register()
                 .listenCloudEvent(kafkaBrokerSecretConsumer.topics().stockReleased(), events::handleStockReleasedEvent)
                 .listenCloudEvent(kafkaBrokerSecretConsumer.topics().stockReserved(), events::handleStockReservedEvent)
                 .listenCloudEvent(kafkaBrokerSecretConsumer.topics().stockFailed(), events::handleStockFailedEvent);
    }
}

package co.com.arka.orders.events;

import co.com.arka.orders.events.config.KafkaBrokerSecretProducer;
import co.com.arka.orders.model.events.OrderConfirmedEvent;
import co.com.arka.orders.model.events.OrderCreatedEvent;
import co.com.arka.orders.model.events.gateways.EventsGateway;
import io.cloudevents.CloudEvent;
import io.cloudevents.core.builder.CloudEventBuilder;
import io.cloudevents.jackson.JsonCloudEventData;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.reactivecommons.api.domain.DomainEventBus;
import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.logging.Level;

import static reactor.core.publisher.Mono.from;

@Log
@RequiredArgsConstructor
@EnableDomainEventBus
public class OrderConfirmedEventGateway implements EventsGateway<OrderConfirmedEvent> {
    private final KafkaBrokerSecretProducer brokerSecret;
    private final DomainEventBus domainEventBus;
    private final ObjectMapper om;

    @Override
    public Mono<Void> emit(OrderConfirmedEvent event) {
        String eventName = brokerSecret.topics().orderConfirmed();
        log.log(Level.INFO, "Sending domain event: {0}: {1}", new String[]{eventName, event.toString()});
        CloudEvent eventCloudEvent = CloudEventBuilder.v1()
                .withId(UUID.randomUUID().toString())
                .withSource(URI.create("https://reactive-commons.org/foos"))
                .withType(eventName)
                .withTime(OffsetDateTime.now())
                .withData("application/json", JsonCloudEventData.wrap(om.valueToTree(event)))
                .build();

        return from(domainEventBus.emit(eventCloudEvent));
    }
}

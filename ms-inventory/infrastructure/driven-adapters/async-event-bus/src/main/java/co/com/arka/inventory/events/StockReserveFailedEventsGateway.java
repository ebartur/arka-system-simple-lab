package co.com.arka.inventory.events;

import co.com.arka.inventory.events.config.KafkaBrokerSecretProducer;
import co.com.arka.inventory.model.events.StockReserveFailedEvent;
import co.com.arka.inventory.model.events.gateways.EventsGateway;
import io.cloudevents.CloudEvent;
import io.cloudevents.jackson.JsonCloudEventData;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.reactivecommons.api.domain.DomainEventBus;
import org.reactivecommons.async.impl.config.annotations.EnableDomainEventBus;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;
import io.cloudevents.core.builder.CloudEventBuilder;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.logging.Level;

import static reactor.core.publisher.Mono.from;

@Log
@RequiredArgsConstructor
@EnableDomainEventBus
public class StockReserveFailedEventsGateway implements EventsGateway<StockReserveFailedEvent> {
    private final KafkaBrokerSecretProducer kafkaBrokerSecretProducer;
    private final DomainEventBus domainEventBus;
    private final ObjectMapper om;

    @Override
    public Mono<Void> emit(StockReserveFailedEvent event) {
        String eventName = kafkaBrokerSecretProducer.topics().stockFailed();
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

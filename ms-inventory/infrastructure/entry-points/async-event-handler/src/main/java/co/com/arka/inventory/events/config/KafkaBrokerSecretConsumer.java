package co.com.arka.inventory.events.config;


import lombok.Builder;

@Builder(toBuilder = true)
public record KafkaBrokerSecretConsumer(
        String bootstrapServers,
        String groupId,
        String autoOffsetReset,
        Topics topics) {
    @Builder(toBuilder = true)
    public record Topics(
            String orderCreated,
            String stockReserved,
            String stockReleased,
            String stockFailed,
            String paymentFailed,
            String orderConfirmed,
            String orderCancelled
    ) {}
}